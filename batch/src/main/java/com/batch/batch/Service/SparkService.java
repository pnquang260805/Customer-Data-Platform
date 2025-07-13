package com.batch.batch.Service;

//import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SparkService {
    @Autowired
    private SparkSession spark;

    @Scheduled(fixedDelay = 10000)
    public void readFile(){
        StructType schema = new StructType()
                .add("customerId", DataTypes.StringType)
                .add("name", DataTypes.StringType)
                .add("address", DataTypes.StringType)
                .add("dob", DataTypes.StringType)
                .add("sex", DataTypes.StringType);

        String url = "s3a://raw/customer/*.json";
        System.out.println("Reading from URL: " + url);
        Dataset<Row> df = spark.read()
                .format("json")
                .option("header", "true")
                .schema(schema)
                .load(url);
        df.show(5, false);
    }
}
