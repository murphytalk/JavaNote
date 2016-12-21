package murphytalk.concurrent;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private final ConcurrentHashMap<K,Wrapper<V>> cache;

    public DumbCache(Function<K, V> provider) {
        if(provider == null) throw new NullPointerException("Need a valid provider!");
        this.provider = provider;
        this.cache = new ConcurrentHashMap<>();
    }

    public V get(K key){
        Wrapper<V> wrapper = cache.get(key);
        if(wrapper ==null){
            //cache miss
            wrapper = new Wrapper();
            Wrapper<V> savedWrapper = cache.putIfAbsent(key, wrapper);
            if(savedWrapper==null) savedWrapper=wrapper;
            synchronized (savedWrapper){
                if(savedWrapper!=wrapper){
                    //if savedWrapper is not the one created by this thread then another thread has already set the data with the same key
                    //in this case the data is ready as long as we get into the critical section (freed by the other thread that just loaded data)
                    //we don't need to do anything, just exit critical section and return the data
                }
                else{
                    //get data from provider and save it, if there is another thread querying the same key now,
                    //it will get blocked until data is set in this thread
                    savedWrapper.value = provider.apply(key);
                }
            }
            return savedWrapper.value;
        }
        else{
            synchronized (wrapper){ //the other thread might be loading data
                return  wrapper.value;
            }
        }
    }
}
