package com.sonatus.scheduler.service;

import com.sonatus.scheduler.domain.CachedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class Reporter {
    private final CachingHelper cachingHelper;

    @Scheduled(fixedDelay = 3000)
    public void reportJobStatus() {
        sendMessage();
    }

    public void sendMessage() {
        String message = "no data";
        List<CachedJob> cachedJobs = cachingHelper.get();

        if (!CollectionUtils.isEmpty(cachedJobs)) {
            message = cachedJobs.stream()
                    .filter(j -> j.getStatus() != null)
                    .map(j -> j.getName() + "(" + j.getStatus().getMessage() + ")")
                    .collect(Collectors.joining(", "));

            if (!StringUtils.hasText(message)) {
                message = "no data";
            }
        }

        log.info("SEND MESSAGE: {}", message);
    }
}
