package murphytalk.test;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;

public class StopWatch {
    public static void measure(String taskName,Runnable task,int repeat){
        final Stopwatch stopwatch  = SimonManager.getStopwatch(taskName);
        for(int i=0;i<repeat;++i) {
            Split split = stopwatch.start();
            task.run();
            split.stop();
        }
        System.out.println(stopwatch.sample());
    }
}
