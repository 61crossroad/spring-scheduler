package com.sonatus.scheduler.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BarJob extends Job {
    private final static String prefix = "BarJob";

    private BarJob(Integer id, String name, Integer duration) {
        super(id, name, duration);
    }

    public static BarJob of(Integer id, Integer duration) {
        return new BarJob(id, String.join(" ",prefix, id.toString()), duration);
    }

    @Override
    public void func() throws Exception {
        log.info("{}'s function implementation, {} sec", this.name, this.duration);
        super.func();
    }
}
