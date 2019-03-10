package murphytalk.concurrent;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Implement a cache : returns value for the given key, if there is a cache miss then call user defined provider to load data.
 *
 * Condition and requirements are:
 * 1) The provider could take long time to return a value,
 *    so if for a particular key the data is not available, do not call the provider again for the further query to the same key
 * 2) In multi-threaded situation
 *    if two treads try to get the value of the same key and the value is not in cache yet, the 2nd thread should block until data is available
 *    if two treads try to get the values of different keys and the values are not in cache,
 *       both of the threads should call provider to load data to cache without blocking each other
 *
 * @param <K>  key data type
 * @param <V>  value data type
 */
public interface DumbCache<K,V>{
    void init(Function<K, V> provider);
    V get(K key);
}

abstract class AbstractDumbCache<K,V> implements DumbCache<K,V>{
    protected static final Logger logger = LoggerFactory.getLogger(AbstractDumbCache.class);
    protected Function<K,V>  provider;

    @Override
    public void init(Function<K, V> provider) {
        if(provider == null) throw new NullPointerException("Need a valid provider!");
        this.provider = provider;
    }
}

class DumbCacheWrapper<K,V> extends AbstractDumbCache<K,V>{
    private static class Wrapper<V> {
        V value;
    }
    private ConcurrentMap<K,Wrapper<V>> cache;

    public void init(Function<K, V> provider) {
        super.init(provider);
        this.cache = new ConcurrentHashMap<>();

    }

    public V get(K key){
        logger.info("get key {}",key);
        Wrapper<V> wrapper = cache.get(key);
        if(wrapper ==null){
            logger.info("cache miss");
            wrapper = new Wrapper();
            Wrapper<V> wrapperInCache;
            synchronized (this) {
                wrapperInCache = cache.putIfAbsent(key, wrapper);
                if (wrapperInCache == null) wrapperInCache = wrapper;
            }
            synchronized (wrapperInCache){
                if(wrapperInCache!=wrapper){
                    logger.info("got lock,data is set in other thread");
                    //if wrapperInCache is not the one created by this thread then another thread has already set the data with the same key
                    //in this case the data is ready as long as we get into the critical section (freed by the other thread that just loaded data)
                    //we don't need to do anything, just exit critical section and return the data
                }
                else{
                    //get data from provider and save it, if there is another thread querying the same key now,
                    //it will get blocked until data is set in this thread
                    logger.info("got lock,now set data");
                    wrapperInCache.value = provider.apply(key);
                }
            }
            logger.info("data is {}",wrapperInCache.value);
            return wrapperInCache.value;
        }
        else{
            logger.info("key already in cache");
            synchronized (wrapper){ //the other thread might be loading data
                logger.info("got data {} from cache", wrapper.value);
                return  wrapper.value;
            }
        }
    }
}

