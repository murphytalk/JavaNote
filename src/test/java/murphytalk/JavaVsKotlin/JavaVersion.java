package murphytalk.JavaVsKotlin;

import murphytalk.test.StopWatch;
import org.junit.Test;

public class JavaVersion {
    @FunctionalInterface
    public interface Handler<E> {

        /**
         * Something has happened, so handle it.
         *
         * @param event  the event to handle
         */
        void handle(E event);
    }

    public static void CallHandler(int v, Handler<Integer> h){
        h.handle(v);
    }

    public static class NOOP{
        private int v;
        NOOP(int vv){v=vv;}
        void nop(int v){}
    }

    @Test
    public void testLambda(){
        StopWatch.measureSimple("JavaLambda", () -> {
            final int i = 0;
            CallHandler( 100, x -> {
                int v = i + 100;
                var nop = new NOOP(v);
                nop.nop(v);
            } );
        }, 10_000_000);
    }
}
