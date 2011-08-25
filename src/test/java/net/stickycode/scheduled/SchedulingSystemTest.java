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

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class SchedulingSystemTest {

  public static class ScheduleTestObject
      implements ScheduledRunnable {

    int counter = 0;

    public void run() {
      counter++;
    }

    @Override
    public Schedule getSchedule() {
      return new Schedule(0, 1);
    }
  }

  @Test
  public void runit()
      throws InterruptedException {
    SingleThreadSchedulingSystem system = new SingleThreadSchedulingSystem();
    ScheduleTestObject runnable = new ScheduleTestObject();
    assertThat(runnable.counter).isEqualTo(0);
    system.schedule(runnable);
    assertThat(runnable.counter).isEqualTo(0);
    system.start();
    Thread.sleep(500);
    assertThat(runnable.counter).isEqualTo(1);
    Thread.sleep(1000);
    assertThat(runnable.counter).isEqualTo(2);
    system.stop();
    assertThat(runnable.counter).isEqualTo(2);
  }
}
