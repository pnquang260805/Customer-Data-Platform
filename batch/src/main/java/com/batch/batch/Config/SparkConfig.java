package com.batch.batch.Config;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {
    @Value("${key.s3.secret}")
    String SECRET_KEY;
    @Value("${key.s3.access}")
    String ACCESS_KEY;
    @Value("${others.host}")
    String HOST;

    @Bean
    public SparkSession spark(){
        SparkSession spark = SparkSession.builder()
                .appName("Test")
                .master("spark://"+HOST+":7077")
                .config("spark.driver.host", HOST)
                .config("spark.ui.enabled", "false")
                .config("spark.driver.bindAddress", "0.0.0.0")
                .config("spark.hadoop.fs.s3a.endpoint", "http://"+HOST+":9000")
                .config("spark.hadoop.fs.s3a.path.style.access", "true")
                .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false")
                .config("spark.hadoop.fs.s3a.access.key", ACCESS_KEY)
                .config("spark.hadoop.fs.s3a.secret.key", SECRET_KEY)
                .getOrCreate();
        return spark;
    }
}
