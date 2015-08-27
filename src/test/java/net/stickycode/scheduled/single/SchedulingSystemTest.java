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

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import net.stickycode.bootstrap.ComponentContainer;
import net.stickycode.coercion.CoercionFinder;
import net.stickycode.coercion.CoercionTarget;
import net.stickycode.configuration.ResolvedConfiguration;
import net.stickycode.scheduled.Schedule;
import net.stickycode.scheduled.ScheduledRunnable;
import net.stickycode.scheduled.ScheduledRunnableRepository;

public class SchedulingSystemTest {

  public static class ScheduleTestObject
      implements ScheduledRunnable {

    int counter = 0;

    @Override
    public void run() {
      counter++;
    }

    @Override
    public Schedule getSchedule() {
      return new Schedule() {

        @Override
        public TimeUnit getUnits() {
          return TimeUnit.SECONDS;
        }

        @Override
        public long getPeriod() {
          return 1;
        }

        @Override
        public long getInitialDelay() {
          return 0;
        }

        @Override
        public boolean isEnabled() {
          return true;
        }
      };
    }

    @Override
    public void applyCoercion(CoercionFinder coercions) {
    }

    @Override
    public void update() {
    }

    @Override
    public void invertControl(ComponentContainer container) {
    }

    @Override
    public boolean requiresResolution() {
      return false;
    }

    @Override
    public ResolvedConfiguration getResolution() {
      return null;
    }

    @Override
    public Object getTarget() {
      return null;
    }

    @Override
    public void resolvedWith(ResolvedConfiguration resolved) {
    }

    @Override
    public CoercionTarget getCoercionTarget() {
      return null;
    }

    @Override
    public List<String> join(String delimeter) {
      return null;
    }
  }

  @Injectable
  ScheduledRunnableRepository repository;

  @Tested
  SingleThreadPoolSchedulingSystem system;

  @Test
  public void runit()
      throws InterruptedException {
    ScheduleTestObject runnable = new ScheduleTestObject();
    assertThat(runnable.counter).isEqualTo(0);

    new Expectations() {
      {
        repository.iterator();
        result = iterator(runnable);
      }
    };

    assertThat(runnable.counter).isEqualTo(0);
    system.start();
    Thread.sleep(500);
    assertThat(runnable.counter).isEqualTo(1);
    Thread.sleep(1000);
    assertThat(runnable.counter).isEqualTo(2);
    system.stop();
    assertThat(runnable.counter).isEqualTo(2);
  }

  private Iterator<ScheduledRunnable> iterator(ScheduleTestObject runnable) {
    List<ScheduledRunnable> list = new LinkedList<ScheduledRunnable>();
    list.add(runnable);
    return list.iterator();
  }
}
