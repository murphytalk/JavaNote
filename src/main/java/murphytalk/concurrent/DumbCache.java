package murphytalk.concurrent;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Function;

/**
 * Condition and requirements are:
 * 1) The provider could take long time to return a value
 * 2) In multi-threaded situation
 *    if two treads try to get the value of the same key and the value is not in cache yet, the 2nd thread should block until data is available
 *    if two treads try to get the value of different keys and the values are not in cache, both of the threads should call provider to load data to cache
 *
 * @param <K>  key data type
 * @param <V>  value data type
 */

public class DumbCache <K,V> {
    private static class Wrapper<V> {
        V value;
    }

    private final Function<K,V>  provider;
    private final Map<K,Wrapper<V>> cache;

    public DumbCache(Function<K, V> provider) {
        if(provider == null) throw new NullPointerException("Need a valid provider!");
        this.provider = provider;
        this.cache = Maps.newConcurrentMap();
    }

    public V get(K key){
        Wrapper<V> wrapper = cache.get(key);
        if(wrapper ==null){
            //cache miss
            wrapper = new Wrapper();
            cache.put(key, wrapper);
            synchronized (wrapper){
                wrapper.value = provider.apply(key);
            }
            return  wrapper.value;
        }
        else{
            synchronized (wrapper){
                return  wrapper.value;
            }
        }
    }
}
