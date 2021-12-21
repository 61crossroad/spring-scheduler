package com.sonatus.scheduler.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CachedJob {
    private String name;
    private JobStatus status;

    private CachedJob(String name) {
        this.name = name;
    }
    public static CachedJob of(String name) {
        return new CachedJob(name);
    }
}
