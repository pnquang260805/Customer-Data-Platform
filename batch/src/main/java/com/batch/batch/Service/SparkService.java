package com.batch.batch.Service;

import com.batch.batch.Options.SaveFileOptions;
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

    public Dataset<Row> readFile(String url, String format){
        try{
            log.info("Calling readFile");
            return spark.read()
                    .format(format)
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .load(url);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveFile(SaveFileOptions options){
        var writer = options.getDf().write()
                .format(options.getFormat())
                .mode(options.getMode());
        if(options.getHeader()){
            writer = writer.option("header", "true");
        }
        if(options.getPartitionCol() != null){
            writer = writer.partitionBy(options.getPartitionCol());
        }
        writer.save(options.getUrl());
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
