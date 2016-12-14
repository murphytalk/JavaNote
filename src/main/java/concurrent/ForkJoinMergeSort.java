package concurrent;

import algo.MergeSort;
import algo.Sort;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 * Using
 */
public class ForkJoinMergeSort extends RecursiveAction {
    private final static int SMALL_ENOUGH = 16;
    private final Comparable[] data;
    private final int start, end;
    public ForkJoinMergeSort(Comparable[] data,int start,int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    public int size(){
        return end - start;
    }

    private void merge(Comparable left, Comparable right){
//        Sort sorter = new MergeSort();
        //sorter.sort(data,left,right);
    }

    protected void compute() {
        final int size = size();
        if(size<SMALL_ENOUGH){
            Arrays.sort(data,0,size);
        }
        else{
            final int mid = size / 2;
            ForkJoinMergeSort left  = new ForkJoinMergeSort(data,start,start+mid);
            ForkJoinMergeSort right = new ForkJoinMergeSort(data,start+mid,end);
            invokeAll(left,right);
        }
    }
}
