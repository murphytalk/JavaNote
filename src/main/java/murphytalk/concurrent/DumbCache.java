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
    private static class Pair<V> {
        V value;
        boolean available;
    }

    private final Function<K,V>  provider;
    private final Map<K,Pair<V>> cache;

    public DumbCache(Function<K, V> provider) {
        if(provider == null) throw new NullPointerException("Need a valid provider!");
        this.provider = provider;
        this.cache = Maps.newConcurrentMap();
    }

    public V get(K key){
        Pair<V> pair = cache.get(key);
        if(pair==null){
            //cache miss
            pair = new Pair();
            cache.put(key,pair);
            synchronized (pair){
                pair.value = provider.apply(key);
                pair.available = pair.value !=null;
            }
            return pair.available?pair.value:null;
        }
        else{
            synchronized (pair){
                return pair.available?pair.value:null;
            }
        }
    }
}
