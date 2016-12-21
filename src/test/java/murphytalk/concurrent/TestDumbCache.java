package murphytalk.concurrent;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestDumbCache {
    private DumbCache<Integer,String> cache;
    private final long DELAY = 3000; //ms

    @Before
    public void setup() {
        cache = new DumbCache<>(
                // this provider converts key to string if key is larger than 10 otherwise return null (to simulate unavailable data)
                // and it takes DELAY ms to return if key is larger than 100 - see the tread testing below
                key -> {
                    try {
                        if(key>100) Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return key > 10 ? String.format("%d", key) : null;
                }
        );
    }

    @Test
    public void testCacheMiss(){
        Function<String,String> provider = mock(Function.class);
        DumbCache<String,String> emptyCache = new DumbCache<>(provider);

        final String key = "XYZ";
        emptyCache.get(key);

        //when there is a cache miss, the provider will get called once with the key
        verify(provider,times(1)).apply(key);
    }

    @Test
    public void testUnavailableData(){
        //the 1st run will take at least  DELAY ms
        long start = System.currentTimeMillis();
        System.out.println(String.format("1st get took %d ms",System.currentTimeMillis() - start));

        assertThat(cache.get(1),is(nullValue())); //no data

        //the 2nd run should return immediately
        start = System.currentTimeMillis();
        assertThat(cache.get(1),is(nullValue())); //no data
        long delta = System.currentTimeMillis() - start;
        System.out.println(String.format("2nd get took %d ms",delta));

        assertTrue(delta<DELAY); //no data but return quickly
    }

    @Test
    public void testTwoTreadsSameKey() throws InterruptedException {
        final Integer key = 12345;
        final String  value = "12345";

        Thread t1 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key);
            System.out.println(String.format("thread 1 took %d ms",System.currentTimeMillis()-start));

            assertThat(s,is(value));
        });

        Thread t2 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key);
            long delta = System.currentTimeMillis()-start;
            System.out.println(String.format("thread 2 took %d ms",delta));

            //thread 2 will get blocked for at least DELAY sec
            assertTrue(delta>=DELAY);
            assertThat(s,is(value));
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @Test
    public void testTwoTreadsDiffKey() throws InterruptedException {
        final Integer key1 = 12345;
        final String  value1 = "12345";

        final Integer key2 = 50;   //see set(), key < 100, so this provider should return immediately
        final String  value2 = "50";

        Thread t1 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key1);
            System.out.println(String.format("thread 1 took %d ms",System.currentTimeMillis()-start));

            assertThat(s,is(value1));
        });

        Thread t2 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key2);
            long delta = System.currentTimeMillis()-start;
            System.out.println(String.format("thread 2 took %d ms",delta));

            assertTrue(delta<DELAY); //thread 2 should not get blocked
            assertThat(s,is(value2));
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
