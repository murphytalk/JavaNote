package murphytalk.concurrent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TestDumbCache {
    private static final Logger logger = LoggerFactory.getLogger(TestDumbCache.class);
    private final DumbCache<Integer,String> cache;
    private static final long DELAY = 3000; //ms

    @Parameterized.Parameters
    public static Collection<Class> instantiate(){
        return Arrays.asList(new Class[] {/*DumbCacheCondition.class,*/ DumbCacheWrapper.class});
    }

    public TestDumbCache(Class testClass) throws IllegalAccessException, InstantiationException {
        cache = (DumbCache<Integer,String>)testClass.newInstance();
        cache.init (
                // this provider converts key to string if key is larger than 10 otherwise return null (to simulate unavailable data)
                // and it takes DELAY ms to return if key is larger than 100 or less than 10 - see the thread testing below
                key -> {
                    try {
                        if(key<10 || key>100) Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return key > 10 ? String.format("%d", key) : null;
                }
        );
    }

    /*
    @Test(expected=NullPointerException.class)
    public void testNullProvider(){
        new DumbCacheWrapper<String,String>(null);
    }
    */

    @Test
    public void testUnavailableData(){
        //the 1st run will take at least  DELAY ms
        long delta ;
        long start = System.currentTimeMillis();

        assertThat(cache.get(1),is(nullValue())); //see setup() : key < 10, provider won't return data,
        delta = System.currentTimeMillis() - start;
        logger.info("1st get took {} ms",delta);

        assertTrue(delta>=DELAY); //1st run will get delayed by provider

        //the 2nd run against the same key (provider has no data for it) should return immediately without calling provider
        start = System.currentTimeMillis();
        assertThat(cache.get(1),is(nullValue())); //no data
        delta = System.currentTimeMillis() - start;
        logger.info("2nd get took {} ms",delta);

        assertTrue(delta<DELAY); //no data but return quickly
    }

    @Test
    public void testTwoTreadsSameKey() throws InterruptedException {
        final Integer key = 12345;
        final String  value = "12345";

        Thread t1 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key);
            logger.info("thread 1 took {} ms",System.currentTimeMillis()-start);

            assertThat(s,is(value));
        });

        Thread t2 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key);
            long delta = System.currentTimeMillis()-start;
            logger.info("thread 2 took {} ms",delta);

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
        final Integer key1 = 12345; //see setup(), key> 100, so this provider should take at least DELAY ms to finish
        final String  value1 = "12345";

        final Integer key2 = 50;   //see setup(), key < 100, so this provider should return immediately
        final String  value2 = "50";

        Thread t1 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key1);
            long delta = System.currentTimeMillis()-start;
            logger.info("thread 1 took {} ms",System.currentTimeMillis()-start);

            assertTrue(delta>=DELAY); //thread 1 was blocked
            assertThat(s,is(value1));
        });

        Thread t2 = new Thread( () ->{
            long start = System.currentTimeMillis();
            String s = cache.get(key2);
            long delta = System.currentTimeMillis()-start;
            logger.info("thread 2 took {} ms",delta);

            assertTrue(delta<DELAY); //thread 2 should not get blocked, even thread1 takes DELAY ms to finish
            assertThat(s,is(value2));
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
