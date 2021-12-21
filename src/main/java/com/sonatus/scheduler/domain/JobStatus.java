package com.sonatus.scheduler.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JobStatus {
    QUEUED("queued"),
    RUNNING("running"),
    SUCCESS("success"),
    FAILURE("failure"),
    SKIPPED("skipped");

    private final String message;
}
