/**
 * Copyright (c) 2011 RedEngine Ltd, http://www.RedEngine.co.nz. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package net.stickycode.scheduled.single;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.stickycode.scheduled.Schedule;
import net.stickycode.scheduled.ScheduledRunnable;
import net.stickycode.scheduled.ScheduledRunnableRepository;
import net.stickycode.scheduler.BackgroundExecutor;
import net.stickycode.stereotype.StickyComponent;
import net.stickycode.stereotype.configured.AfterConfiguration;
import net.stickycode.stereotype.configured.Configured;
import net.stickycode.stereotype.configured.PostConfigured;

@StickyComponent
public class SingleThreadPoolSchedulingSystem
    implements BackgroundExecutor {

  private Logger log = LoggerFactory.getLogger(SingleThreadPoolSchedulingSystem.class);

  private ScheduledExecutorService executor;

  @Inject
  private ScheduledRunnableRepository schedules;

  @Configured
  private Integer shutdownTimeoutInSeconds = 5;

  @Configured
  private Integer maximumThreads = 3;

  @PostConfigured
  public void setupExecutor() {
    executor = new StickyThreadPoolExcutor(maximumThreads);
  }

  @AfterConfiguration
  public void setupSchedules() {
    log.info("starting schedules");
    for (ScheduledRunnable runnable : schedules) {
      Schedule s = runnable.getSchedule();
      if (s.isEnabled()) {
        log.debug("scheduling {} {}", runnable, s);
        executor.scheduleAtFixedRate(runnable, s.getInitialDelay(), s.getPeriod(), s.getUnits());
      }
      else {
        log.info("not scheduling {} as it is disabled");
      }
    }
  }

  @PreDestroy
  public void stop() {
    if (executor != null)
      stopping();
  }

  private void stopping() {
    log.info("stopping schedules");
    try {
      executor.shutdown();
      if (!executor.awaitTermination(shutdownTimeoutInSeconds, TimeUnit.SECONDS))
        forceShutdown();
    }
    catch (InterruptedException e) {
      forceShutdown();
    }
  }

  private void forceShutdown() {
    List<Runnable> leftovers = executor.shutdownNow();
    log.error("Failed to shutdown {} tasks on shutdown {}", leftovers.size(), leftovers);
  }

  @Override
  public void execute(Runnable job) {
    log.info("execute {} in background", job);
    executor.execute(job);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    log.info("execute {} in background", task);
    return executor.submit(task);
  }

}
