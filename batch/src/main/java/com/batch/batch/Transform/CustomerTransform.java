package com.batch.batch.Transform;

import com.batch.batch.Options.SaveFileOptions;
import com.batch.batch.Service.SparkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static org.apache.spark.sql.functions.*;

import com.batch.batch.Utils.CountryNameLUT;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CustomerTransform {
    @Autowired
    private SparkService sparkService;
    @Autowired
    private CountryNameLUT countryNameLUT;
    @Autowired

    public void customerToSilver() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String schema = "customerId STRING, name STRING, email STRING, addressLine1 STRING,addressLine2 STRING, dob STRING, country STRING, sex STRING";

        String raw_url = "s3a://bronze/customer/" + yesterday.toString() + "/*.json";
        String destinationUrl = "s3a://silver/customer_" + yesterday.toString();

        Dataset<Row> df = sparkService.readFile(raw_url, schema, "json");

        df = df.na().drop("any", new String[]{"customerId"});
        df = df.dropDuplicates();
        df = df.withColumn("dob", to_date(col("dob"), "yyyy-MM-dd"));

        Map<String, String> countries = countryNameLUT.countryLUT();
        UDF1<String, String> countryNameUDF = (String countryName) -> {
            if (countryName == null)
                return "Unknown";
            String c = countryName.toLowerCase();
            return countries.getOrDefault(c, "Unknown");
        };
        sparkService.udfRegistration("countryNameUdf", countryNameUDF, DataTypes.StringType);

        Dataset<Row> gender = df.groupBy("sex").count();
        String imputeGender = "Unknown"; // fallback
        List<Row> genderList = df.groupBy("sex").count()
                .orderBy(desc("count"))
                .collectAsList();
        if (!genderList.isEmpty()) {
            imputeGender = genderList.get(0).getString(0); // lấy giới tính xuất hiện nhiều nhất
        }

        Map<String, Object> fillMap = new HashMap<>();
        fillMap.put("name", "Unknown");
        fillMap.put("email", "Unknown");
        fillMap.put("addressLine1", "Unknown");
        fillMap.put("addressLine2", "Unknown");
        fillMap.put("sex", imputeGender);
        df = df.na().fill(fillMap);

        df = df.na().drop("any", new String[]{"dob"});
        df = df.withColumn("createdDate", lit(yesterday));

        System.out.println("After transform raw df");
        df.show(5, false);

        sparkService.saveFile(
                SaveFileOptions.builder()
                        .url(destinationUrl)
                        .df(df)
                        .header(true)
                        .build()
        );
    }

        public void aggregateCustomer() {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String silverUrl = "s3a://silver/customer_" + yesterday.toString();

            String schema = "customerId STRING, name STRING, email STRING, addressLine1 STRING,addressLine2 STRING, dob DATE, country STRING, sex STRING, createdDate STRING";

            Dataset<Row> df = sparkService.readFile(silverUrl, schema, "parquet");

            System.out.println("Read silver");
            df.show(5, false);

            System.out.println("Aggregated");
            Dataset<Row> groupedDf = df
                    .groupBy(col("createdDate"))
                    .agg(count("customerId").alias("numberOfNewCustomer"))
                    .select(col("createdDate"), col("numberOfNewCustomer"));

            groupedDf.show(5, false);

            String destinationUrl = "s3a://gold/new-customer";
            sparkService.saveFile(SaveFileOptions.builder()
                    .mode("Append")
                    .format("parquet")
                    .df(groupedDf)
                    .url(destinationUrl)
                    .header(true)
                    .build());
        }
}
