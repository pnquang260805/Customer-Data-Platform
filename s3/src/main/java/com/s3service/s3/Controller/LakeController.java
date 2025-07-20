package com.s3service.s3.Controller;

import com.s3service.s3.Service.LakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class LakeController {
    private final LakeService lakeService;

    @GetMapping("/check-prefix")
    public ResponseEntity<Boolean> checkPrefix(@RequestParam String bucket, @RequestParam String prefix){
        return ResponseEntity.ok().body(lakeService.folderExisted(bucket, prefix));
    }
}
