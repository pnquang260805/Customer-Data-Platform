package com.batch.batch.Transform;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import static org.apache.spark.sql.functions.*;

import com.batch.batch.Service.SparkService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductTransform {
    @Autowired
    private SparkService sparkService;

    @Autowired
    private WebClient webClient;

    @Scheduled(fixedDelay = 5000)
    public void productToSilver() {
        // LocalDate yesterday = LocalDate.now().minusDays(1);
        log.error("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
        LocalDate yesterday = LocalDate.now();
        String prefix = "product/" + yesterday.toString();
        String bucketName = "bronze";

        String fullPath = "http://localhost:8083/api/s3/check-prefix?bucket=" + bucketName + "&prefix=" + prefix;
        Boolean prefixExist = webClient.get()
                .uri(fullPath)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(prefixExist)) {
            log.info(yesterday.toString() + " does not have new customer");
            return;
        }

        String bronzeUrl = "s3a://bronze/product/" + yesterday.toString() + "/*.json";
        String destinationUrl = "s3a://silver/product/" + yesterday.toString();

        String schema = "productId STRING, productName STRING, unitPrice DOUBLE, quantityInStock LONG";

        Dataset<Row> df = sparkService.readFile(bronzeUrl, schema, "json");

        // Drop null productId
        df = df.na().drop("any", new String[]{"productId"});

        Double avgPrice = df.select("unitPrice")
                .where(col("unitPrice").isNotNull())
                .agg(mean("unitPrice"))
                .first().getDouble(0);

        // Calculate median quantity
        Dataset<Row> quantities = df.select("quantityInStock")
                .where(col("quantityInStock").isNotNull())
                .orderBy("quantityInStock");
        int medIdx = (int)(quantities.count() / 2);
        Long medQIS = quantities.collectAsList().get(medIdx).getLong(0);

        Map<String, Object> fillMap = new HashMap<>();
        fillMap.put("productName", "Unknow");
        fillMap.put("unitPrice", avgPrice);
        fillMap.put("quantityInStock", medQIS);
        Dataset<Row> afterHandledDF = df.na().fill(fillMap);
        afterHandledDF.show(5, false);

        sparkService.saveFile(afterHandledDF, destinationUrl, "parquet");
        log.error("AJKBBBBBBBBBBBBBB");
    }
}
