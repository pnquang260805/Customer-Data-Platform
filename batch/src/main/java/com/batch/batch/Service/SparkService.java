package com.batch.batch.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SparkService {
    @Autowired
    private SparkSession spark;

    public Dataset<Row> readFile(String url, String schema, String format){
        try{
            log.info("Calling readFile");
            return spark.read()
                    .format(format)
                    .option("header", "true")
                    .schema(schema)
                    .load(url);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveFile(Dataset<Row> df, String url, String format){
        try{

            log.info("start saving");
            df.write().format(format).mode("Overwrite").option("header", "true").save(url);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public <K, V> void udfRegistration(String udfName , UDF1<K, V> udf, DataType type){
        try{
            log.info("Registering udf");
            spark.udf().register(udfName, udf, type);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
