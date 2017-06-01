package murphytalk.concurrent;

import murphytalk.test.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import sun.misc.Contended;

import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * https://software.intel.com/en-us/articles/avoiding-and-identifying-false-sharing-among-threads
 *
 */
public class TestFalseSharing {
    private static final long ITERATIONS = 1_000_000_000;
    private static final long REPEAT = 1;

    private static class NoVolatile{
        public long valueA;
        public long valueB;
    }

    private static class NoCacheLinePadding{
        public volatile long valueA;
        public volatile long valueB;
    }

    private static class HasCacheLinePadding{
        @Contended
        public volatile long valueA;
        public volatile long valueB;
    }

    private static class LhsPadding
    {
        protected long p1, p2, p3, p4, p5, p6, p7;
    }

    private static class Value extends LhsPadding
    {
        protected volatile long valueA;
        protected volatile long valueB;
    }

    private static class RhsPadding extends Value
    {
        protected long p9, p10, p11, p12, p13, p14, p15;
    }

    private static  class LikeDisruptorSequence extends RhsPadding{
        void incA(){valueA++;}
        void incB(){valueB++;}
    }


    private static void updateValues(Runnable readerTask,Runnable writerTask)  {
        Thread reader = new Thread(readerTask);
        Thread writer = new Thread(writerTask);

        reader.start();
        writer.start();

        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void repeat(Runnable runnable){
        for(long i =0;i<ITERATIONS;++i){
            runnable.run();
        }
    }

    @Test @Ignore
    public void runWithoutPadding() throws Exception{
        final NoCacheLinePadding unpadded = new NoCacheLinePadding();
        StopWatch.measureSimple("runWithoutPadding",
                () -> updateValues( ()-> repeat(()->unpadded.valueA++),
                                    ()-> repeat(()->unpadded.valueB++))
        , REPEAT);
    }

    @Test @Ignore
    public void runWithPadding() throws Exception{
        final HasCacheLinePadding padded = new HasCacheLinePadding();
        StopWatch.measureSimple("runWithPadding",
                () -> updateValues( ()-> repeat(()->padded.valueA++),
                                    ()-> repeat(()->padded.valueB++))
        ,REPEAT);
    }

    @Test @Ignore
    public void runWithoutVolatile() throws Exception{
        final NoVolatile v = new NoVolatile();
        StopWatch.measureSimple("runWithoutVolatile",
                () -> updateValues( ()-> repeat(()->v.valueA++),
                                    ()-> repeat(()->v.valueB++))
        , REPEAT);
    }

    @Test @Ignore
    public void runWithLikeDisruptor() throws Exception{
        final LikeDisruptorSequence l = new LikeDisruptorSequence();
        StopWatch.measureSimple("runLikeDisruptor",
                () -> updateValues( ()-> repeat(()->l.incA()),
                                    ()-> repeat(()->l.incB()))
        , REPEAT);
    }
}
