package murphytalk.concurrent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;


@RunWith(Parameterized.class)
public class TestDumbCacheMiss {
    private final DumbCache<String,String> cache;

    @Parameterized.Parameters
    public static Collection<Class> instantiate(){
        return Arrays.asList(new Class[] {DumbCacheWrapper.class});
    }

    public TestDumbCacheMiss(Class<DumbCache<String, String>> cl) throws IllegalAccessException, InstantiationException {
        cache = cl.newInstance();
    }

    @Test
    public void testCacheMiss(){
        final Function<String,String> provider = mock(Function.class);
        cache.init(provider);
        final String key = "XYZ";
        final String value = "data";
        when(provider.apply(key)).thenReturn(value);

        //first all to cache ,should get the value (lazy load)
        assertThat(cache.get(key),is(value));
        //there is a cache miss, the provider should get called once with the key
        verify(provider,times(1)).apply(key);

        //reset the invoker counter
        reset(provider);

        //2nd call to cache, this time no more miss
        assertThat(cache.get(key),is(value));
        //since there is no cache miss, the provider should not get called with the key
        verify(provider,times(0)).apply(key);
    }
 }
