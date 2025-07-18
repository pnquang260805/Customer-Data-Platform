package com.batch.batch.Transform;

import com.batch.batch.Service.S3Service;
import com.batch.batch.Service.SparkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataType;
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
    private S3Service s3Service;

    @Scheduled(cron = "0 0 * * * ?")
    public void customerToSilver() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        try {
            String schema = "customerId STRING, name STRING, email STRING, addressLine1 STRING,addressLine2 STRING, dob STRING, country STRING, sex STRING";
            if (!s3Service.checkFolderExists("raw", "customer/" + yesterday.toString())) {
                System.out.println(yesterday.toString() + " does not have new customer");
                return;
            }

            String raw_url = "s3a://raw/customer/" + yesterday.toString() + "/*.json";
            String destinationUrl = "s3a://silver/customer-" + yesterday.toString();

            Dataset<Row> df = sparkService.readFile(raw_url, schema, "json");

            df = df.na().drop("any", new String[]{"customerId"});
            df = df.dropDuplicates();
            df = df.withColumn("dob", to_date(col("dob"), "yyyy-MM-dd"));

            Map<String, String> countries = countryNameLUT.countryLUT();
            UDF1<String, String> countryNameUDF = (String countryName) -> {
                if (countryName == null) return "Unknown";
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
                imputeGender = genderList.get(0).getString(0);  // lấy giới tính xuất hiện nhiều nhất
            }

            Map<String, Object> fillMap = new HashMap<>();
            fillMap.put("name", "Unknown");
            fillMap.put("email", "Unknown");
            fillMap.put("addressLine1", "Unknown");
            fillMap.put("addressLine2", "Unknown");
            fillMap.put("sex", imputeGender);
            df = df.na().fill(fillMap);

            df = df.na().drop("any", new String[]{"dob"});
            sparkService.saveFile(df, destinationUrl, "parquet");

        } catch (Exception e) {
            log.error("Exception while transforming customer data", e);
        }
    }
}
