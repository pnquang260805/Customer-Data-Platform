package com.batch.batch.Service;

//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SparkService {
    @Autowired
    private SparkSession spark;

//    @Scheduled(fixedDelay = 10000)
//    public void readFile(){
//        String schema = "customerId STRING, name STRING, email STRING, addressLine1 STRING,addressLine2 STRING, dob STRING, country STRING, sex STRING";
//
//        String url = "s3a://raw/customer/*.json";
//        System.out.println("Reading from URL: " + url);
//        Dataset<Row> df = spark.read()
//                .format("json")
//                .option("header", "true")
//                .schema(schema)
//                .load(url);
//        df.show(5, false);
//    }
    public Dataset<Row> readFile(String url, String schema, String format){
        log.info("Calling readFile");
        return spark.read()
                .format(format)
                .option("header", "true")
                .schema(schema)
                .load(url);
    }

    public void saveFile(Dataset<Row> df, String url, String format){
        log.info("start saving");
        df.write().format(format).mode("Overwrite").option("header", "true").save(url);
    }
}
