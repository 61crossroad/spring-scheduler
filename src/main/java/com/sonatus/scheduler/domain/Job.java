package com.sonatus.scheduler.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
@Setter
@Getter
public abstract class Job {
    protected Integer id;
    protected String name;
    protected Integer duration;
    protected JobStatus status;
    protected List<Integer> parents = new ArrayList<>();
    protected List<Integer> children = new ArrayList<>();

    protected Job(Integer id, String name, Integer duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public void func() throws Exception {
        for (int i = 1; i <= this.duration; i++) {
            Thread.sleep(2000);
            log.info("{}...... {} sec", this.name, i);
        }
    }

}
