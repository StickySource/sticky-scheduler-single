package net.stickycode.scheduled.single;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class StickyThreadPoolExcutor
    extends ScheduledThreadPoolExecutor {

  public StickyThreadPoolExcutor(int corePoolSize) {
    super(corePoolSize, new NamedThreadFactory("s"));
  }

}
