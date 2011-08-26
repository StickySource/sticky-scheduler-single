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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.stickycode.scheduled.Schedule;
import net.stickycode.scheduled.ScheduledRunnable;
import net.stickycode.scheduled.ScheduledRunnableRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
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
  
  @Mock
  ScheduledRunnableRepository repository;
  
  @InjectMocks
  SingleThreadSchedulingSystem system = new SingleThreadSchedulingSystem();

  @Test
  public void runit()
      throws InterruptedException {
    ScheduleTestObject runnable = new ScheduleTestObject();
    assertThat(runnable.counter).isEqualTo(0);
    when(repository.iterator()).thenReturn(iterator(runnable));
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
