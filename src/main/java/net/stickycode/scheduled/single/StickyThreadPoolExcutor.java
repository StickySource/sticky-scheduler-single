package net.stickycode.scheduled.single;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StickyThreadPoolExcutor
    extends ScheduledThreadPoolExecutor {

  private Logger log = LoggerFactory.getLogger(getClass());
  
  public StickyThreadPoolExcutor(int corePoolSize) {
    super(corePoolSize, new NamedThreadFactory("sticky"));
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    log.debug("executing {} on {}", r, t);
    super.beforeExecute(t, r);
  }
  
  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    log.debug("executed {}", r, t);
  }

}
