package algo;


import concurrent.ForkJoinMergeSort;
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

    private Integer toBeSorted [];

    static {
        //make sure this only runs once, so the runtime of each individual test are comparable.
        Object [] random = (new Random()).ints(size).boxed().toArray();
        data = Arrays.copyOf( random, random.length, Integer[].class);
        sortedData = data.clone();
        Arrays.sort(sortedData);
    }

    @Before
    public void setup(){
        toBeSorted = data.clone();
    }

    @Test
    public void testMergeSort1(){
        MergeSort mergeSort = new MergeSort(size);
        mergeSort.sort(toBeSorted);
        assertThat(sortedData,is(toBeSorted));
    }

    /*Sort2 leverages fork/join framework to parallelize the sorting.
      30% faster than testMergeSort1() on my 8-core (logical) i7 6700K
    */
    @Test
    public void testMergeSort2(){
        ForkJoinMergeSort sorter = new ForkJoinMergeSort(toBeSorted,0,size);
        ForkJoinPool.commonPool().invoke(sorter);
        assertThat(sortedData,is(toBeSorted));
    }
}
