package com.batch.batch.Options;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveFileOptions {
    Dataset<Row> df;
    String url;
    @Builder.Default
    String mode = "Overwrite";
    @Builder.Default
    String format = "parquet";
    String partitionCol;
    Boolean header;
}
