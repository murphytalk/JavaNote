package murphytalk.test;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.javasimon.utils.SimonUtils;

public class StopWatch {
    private final Stopwatch stopwatch;

    public StopWatch(String name){
        stopwatch = SimonManager.getStopwatch(name);
    }

    public void measure(String taskName,Runnable task){
        Split split = stopwatch.start();
        task.run();
        split.stop();
        System.out.println(taskName+":"+SimonUtils.presentNanoTime(split.runningFor()));
    }
}
