package murphytalk.algo;


import murphytalk.concurrent.ForkJoinMergeSort;
import murphytalk.test.StopWatch;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by murphytalk on 12/10/2016.
 */
public class TestSort {
    private static final int size = 1_000_000;
    private static final Integer data [];
    private static final Integer sortedData [];
    private static final StopWatch stopwatch;

    private Integer toBeSorted [];

    static {
        //make sure this only runs once, so the runtime of each individual test are comparable.
        Object [] random = (new Random()).ints(size).boxed().toArray();
        data = Arrays.copyOf( random, random.length, Integer[].class);
        sortedData = data.clone();
        Arrays.sort(sortedData);
        stopwatch = new StopWatch("TestSort");
    }

    @Before
    public void setup(){
        toBeSorted = data.clone();
    }

    /* The vanilla array sort uses TimSort.
       For this 1 million random int test case, quite often this sort is the slowest
     */
    //@Ignore
    @Test
    public void testArraySort(){
        stopwatch.measure("testArraySort", ()-> Arrays.sort(toBeSorted));
        assertThat(sortedData,is(toBeSorted));
    }

    /* Merge sort in single thread
     */
    @Test
    public void testMergeSort(){
        final MergeSort mergeSort = new MergeSort(size);
        stopwatch.measure("testMergeSort", ()-> mergeSort.sort(toBeSorted));
        assertThat(sortedData,is(toBeSorted));
    }

    /* This leverages fork/join framework to parallelize the sorting.
       30% faster than testMergeSort1() on my 8-core (logical) i7 6700K
    */
    @Test
    public void testMergeSortForkJoin(){
        final ForkJoinMergeSort sorter = new ForkJoinMergeSort(toBeSorted,0,size);
        stopwatch.measure("testMergeSortForkJoin", ()-> ForkJoinPool.commonPool().invoke(sorter));
        assertThat(sortedData,is(toBeSorted));
    }
}
