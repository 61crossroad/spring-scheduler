package com.sonatus.scheduler.service;

import com.sonatus.scheduler.domain.CachedJob;
import com.sonatus.scheduler.domain.Job;
import com.sonatus.scheduler.domain.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CachingHelper {
    private final CacheManager cacheManager;
    @Value("${custom.cache.name}")
    private String name;
    @Value("${custom.cache.key}")
    private String key;

    public List<CachedJob> get() {
        List<CachedJob> cachedJobs = Collections.emptyList();
        Cache cache = cacheManager.getCache(name);
        if (cache != null && cache.get(key) != null) {
            cachedJobs = (List<CachedJob>) Objects.requireNonNull(cache.get(key)).get();
        }

        return cachedJobs;
    }

    public void initialize(List<Job> jobs) {
        List<CachedJob> cachedJobs = jobs.stream()
                .map(j -> CachedJob.of(j.getName()))
                .collect(Collectors.toList());
        Objects.requireNonNull(cacheManager.getCache(name)).put(key, cachedJobs);
    }

    @Async("cachingExecutor")
    public void update(int id, JobStatus status) {
        List<CachedJob> cachedJobs = get();
        cachedJobs.get(id - 1).setStatus(status);
    }
}
