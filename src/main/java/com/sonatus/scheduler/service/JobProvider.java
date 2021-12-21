package com.sonatus.scheduler.service;

import com.opencsv.CSVReader;
import com.sonatus.scheduler.domain.BarJob;
import com.sonatus.scheduler.domain.FooJob;
import com.sonatus.scheduler.domain.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobProvider {
    @Value("${custom.filename}")
    private String filename;

    public List<Job> getJobs(String[] args) {
        try {
            return args.length == 0 ? getJobs(filename) : getJobs(args[0]);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<Job> getJobs(String filename) throws Exception {
        List<Job> jobs = new ArrayList<>();
        Map<Integer, String> dependencies = new HashMap<>();
        File file = new File(ClassLoader.getSystemResource(filename).getFile());
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] nextLine;

        reader.readNext();

        while ((nextLine = reader.readNext()) != null) {
            Job job;
            Integer id = Integer.valueOf(nextLine[0]);
            Integer duration = Integer.valueOf(nextLine[2]);

            if ("foo".equals(nextLine[1])) {
                job = FooJob.of(id, duration);
            } else {
                job = BarJob.of(id, duration);
            }

            jobs.add(job);
            dependencies.put(id, nextLine[3]);
        }

        for (Map.Entry<Integer, String> entry : dependencies.entrySet()) {
            Integer child = entry.getKey();
            List<Integer> parentIds = jobs.get(child - 1).getParents();
            String[] parents = entry.getValue().split("\\s+");

            for (String parent : parents) {
                if (StringUtils.hasText(parent)) {
                    int parentId = Integer.parseInt(parent);
                    parentIds.add(parentId);
                    List<Integer> childrenIds = jobs.get(parentId - 1).getChildren();
                    childrenIds.add(child);
                }
            }
        }

        return jobs;
    }
}
