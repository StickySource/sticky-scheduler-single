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
package net.stickycode.scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.stickycode.stereotype.PostConfigured;
import net.stickycode.stereotype.StickyComponent;

@StickyComponent
public class SingleThreadSchedulingSystem
    implements SchedulingSystem {

private Logger log = LoggerFactory.getLogger(SingleThreadSchedulingSystem.class);

  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("sticky"));

  private List<ScheduledRunnable> schedules = new ArrayList<ScheduledRunnable>();

  @Override
  public void schedule(ScheduledRunnable runnable) {
    schedules.add(runnable);
  }

  @PostConfigured
  public void start() {
    log.info("starting schedules");
    for (ScheduledRunnable runnable: schedules) {
      Schedule s = runnable.getSchedule();
      log.debug("scheduling {}", runnable);
      executor.scheduleAtFixedRate(runnable, s.getInitialDelay(), s.getPeriod(), TimeUnit.SECONDS);
    }
  }

  @PreDestroy
  public void stop() {
    log.info("stopping schedules");
    try {
      executor.shutdown();
      if (!executor.awaitTermination(5, TimeUnit.SECONDS))
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
