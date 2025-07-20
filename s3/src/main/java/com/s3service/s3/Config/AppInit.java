package com.s3service.s3.Config;


import com.s3service.s3.Service.LakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class AppInit implements ApplicationRunner {
    @Autowired
    private LakeService lakeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ArrayList<String> bucketsName = new ArrayList<>(Arrays.asList("bronze", "silver", "gold"));
        bucketsName.forEach(bucketName -> {
            if(!lakeService.bucketExisted(bucketName)){
                lakeService.createBucket(bucketName);
            }
        });
    }
}
