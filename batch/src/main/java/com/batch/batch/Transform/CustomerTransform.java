package com.batch.batch.Transform;

import com.batch.batch.Service.SparkService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
@Slf4j
public class CustomerTransform {
    @Autowired
    private SparkService sparkService;

    @Scheduled(cron = "0 0 * * * ?")
    public void customerToSilver(){
        String schema = "customerId STRING, name STRING, email STRING, addressLine1 STRING,addressLine2 STRING, dob STRING, country STRING, sex STRING";
        String raw_url = "s3a://raw/customer/*.json";

        LocalDate today = LocalDate.now();
        String destUrl = "s3a://silver/customer-"+today.toString();

        Dataset<Row> df = sparkService.readFile(raw_url, schema, "json");
        sparkService.saveFile(df, destUrl, "parquet");
    }
}
