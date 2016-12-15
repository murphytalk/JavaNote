package murphytalk.concurrent;

import murphytalk.algo.MergeSort;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 *  Parallel version of merge sort
 */
public class ForkJoinMergeSort extends RecursiveAction {
    private final Comparable[] data;
    private final Comparable[] work;
    private final int begin, end;

    public ForkJoinMergeSort(Comparable[] data, int begin, int end) {
        this.data = data;
        this.begin = begin;
        this.end = end;
        this.work = new Comparable[end-begin];
    }

    public int size(){
        return end - begin;
    }

    private void merge(int mid){
        MergeSort.merge(data,begin,end,mid,work);
    }

    protected void compute() {
        final int size = size();
        if(size<MergeSort.SMALL_ENOUGH){
            Arrays.sort(data,begin,end);
        }
        else{
            final int mid = begin + size / 2;
            ForkJoinMergeSort left  = new ForkJoinMergeSort(data, begin, mid);
            ForkJoinMergeSort right = new ForkJoinMergeSort(data, mid,end);
            invokeAll(left,right);
            merge(mid);
        }
    }
}
