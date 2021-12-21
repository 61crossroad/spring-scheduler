package com.sonatus.scheduler.runner;

import com.sonatus.scheduler.domain.Job;
import com.sonatus.scheduler.domain.JobStatus;
import com.sonatus.scheduler.service.CachingHelper;
import com.sonatus.scheduler.service.JobExecutor;
import com.sonatus.scheduler.service.JobProvider;
import com.sonatus.scheduler.service.Reporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Component
public class JobScheduler implements ApplicationRunner {
    private final JobProvider jobProvider;
    private final JobExecutor jobExecutor;
    private final CachingHelper cachingHelper;
    private final Reporter reporter;

    @Override
    public void run(ApplicationArguments args) {
        List<Job> jobs = jobProvider.getJobs(args.getSourceArgs());
        List<CompletableFuture<Job>> runningJobs = new ArrayList<>();
        Queue<Job> queue = new LinkedList<>();

        for (Job job : jobs) {
            if (job.getParents().isEmpty()) {
                job.setStatus(JobStatus.QUEUED);
                queue.add(job);
            }
        }
        cachingHelper.initialize(jobs);

        while (!queue.isEmpty()) {
            Job job = queue.poll();
            int statusCount = 0;

            for (Integer childId : job.getChildren()) {
                Job childJob = jobs.get(childId - 1);
                if (childJob.getStatus() == null) {
                    childJob.setStatus(JobStatus.QUEUED);
                    queue.add(childJob);
                }
            }

            for (Integer parentId : job.getParents()) {
                JobStatus parentStatus = jobs.get(parentId - 1).getStatus();
                if (parentStatus == null ||
                        JobStatus.QUEUED.equals(parentStatus) ||
                        JobStatus.RUNNING.equals(parentStatus)) {
                    statusCount = -1;
                    break;
                } else if (JobStatus.FAILURE.equals(parentStatus) || JobStatus.SKIPPED.equals(parentStatus)) {
                    statusCount++;
                }
            }

            if (statusCount == -1) {
                queue.add(job);
            } else if (statusCount > 0) {
                job.setStatus(JobStatus.SKIPPED);
                cachingHelper.update(job.getId(), JobStatus.SKIPPED);
            } else {
                job.setStatus(JobStatus.RUNNING);
                CompletableFuture<Job> execution = jobExecutor.run(job);
                runningJobs.add(execution);
            }
        }

        CompletableFuture<Void> wait = CompletableFuture.allOf(runningJobs.toArray(new CompletableFuture[0]));
        try {
            wait.get();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            reporter.sendMessage();
            log.info("exit scheduler!");
        }

        System.exit(0);
    }
}
