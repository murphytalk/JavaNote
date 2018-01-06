package murphytalk.test;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatch {
    private static final Logger logger = LoggerFactory.getLogger(Stopwatch.class);
    private static void _measure(String taskName, Runnable task, long repeat, boolean sample) {
        final Stopwatch stopwatch = SimonManager.getStopwatch(taskName);
        for (long i = 0; i < repeat; ++i) {
            Split split = stopwatch.start();
            task.run();
            split.stop();
        }
        logger.info ( (sample ? stopwatch.sample() : stopwatch).toString() );
    }

    public static void measure(String taskName, Runnable task, long repeat) {
        _measure(taskName, task, repeat, true);
    }

    public static void measureSimple(String taskName, Runnable task, long repeat) {
        _measure(taskName, task, repeat, false);
    }

    public static void nanoTick(String taskName, Runnable task, long repeat) {
        final long start = System.nanoTime();
        for (long i = 0; i < repeat; ++i) {
            task.run();
        }
        logger.info ("{} : average {} nanosecond", taskName, (System.nanoTime() - start) / repeat);
    }
}
