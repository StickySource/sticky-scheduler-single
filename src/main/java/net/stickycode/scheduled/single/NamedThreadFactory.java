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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory
    implements ThreadFactory {

  final ThreadGroup group;

  final AtomicInteger threadCounter = new AtomicInteger(1);

  final String prefix;

  public NamedThreadFactory(String prefix) {
    this.prefix = prefix;
    SecurityManager s = System.getSecurityManager();
    if (s != null)
      group = s.getThreadGroup();
    else
      group = Thread.currentThread().getThreadGroup();
  }

  public Thread newThread(Runnable r) {
    return new Thread(group, r, prefix + "-" + threadCounter.getAndIncrement(), 0);
  }

}
