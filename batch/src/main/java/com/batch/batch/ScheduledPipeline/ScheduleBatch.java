package com.batch.batch.ScheduledPipeline;

import com.batch.batch.Transform.CustomerTransform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleBatch {
    private final CustomerTransform customerTransform;
    private final WebClient webClient;

//    @Scheduled(cron = "0 0 * * * ?")
@EventListener(ApplicationReadyEvent.class)
    @Async
    public void runCustomerPipeline(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String prefix = "customer/" + yesterday.toString();
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
        customerTransform.customerToSilver();
        customerTransform.aggregateCustomer();
    }
}
