package com.sonatus.scheduler.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FooJob extends Job {
    private final static String prefix = "FooJob";

    private FooJob(Integer id, String name, Integer duration) {
        super(id, name, duration);
    }

    public static FooJob of(Integer id, Integer duration) {
        return new FooJob(id, String.join(" ",prefix, id.toString()), duration);
    }

    @Override
    public void func() throws Exception {
        log.info("{}'s function implementation, {} sec", this.name, this.duration);
        super.func();
    }
}
