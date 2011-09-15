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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import net.stickycode.scheduled.Schedule;
import net.stickycode.scheduled.ScheduledRunnable;
import net.stickycode.scheduled.ScheduledRunnableRepository;
import net.stickycode.scheduled.SchedulingSystem;
import net.stickycode.stereotype.Configured;
import net.stickycode.stereotype.PostConfigured;
import net.stickycode.stereotype.StickyComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StickyComponent
public class SingleThreadSchedulingSystem
    implements SchedulingSystem {

  private Logger log = LoggerFactory.getLogger(SingleThreadSchedulingSystem.class);

  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("sticky"));

  @Inject
  private ScheduledRunnableRepository schedules;

  @Configured
  private Integer shutdownTimeoutInSeconds = 5;

  @PostConfigured
  public void start() {
    log.info("starting schedules");
    for (ScheduledRunnable runnable : schedules) {
      Schedule s = runnable.getSchedule();
      log.debug("scheduling {} {}",runnable, s); 
      executor.scheduleAtFixedRate(runnable, s.getInitialDelay(), s.getPeriod(), s.getUnits());
    }
  }

  @PreDestroy
  public void stop() {
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
}
