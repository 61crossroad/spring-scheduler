package com.sonatus.scheduler.service;

import com.sonatus.scheduler.domain.Job;
import com.sonatus.scheduler.domain.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobExecutor {
    private final CachingHelper cachingHelper;

    @Async
    public CompletableFuture<Job> run(Job job) {
        try {
            job.func();
            job.setStatus(JobStatus.SUCCESS);

            return CompletableFuture.completedFuture(job);
        } catch (Exception e) {
            log.error(e.getMessage());
            job.setStatus(JobStatus.FAILURE);

            return CompletableFuture.failedFuture(e);
        } finally {
            cachingHelper.update(job.getId(), job.getStatus());
        }
    }
}
