package murphytalk.algo;


public interface Sort<T extends Comparable> {
    @FunctionalInterface
    interface Merge<T extends Comparable>{
        void merge(T[] objects,int begin,int end,int mid,T[] work);
    }

    default void sort(T[] objects) {
        sort(objects,0,objects.length);
    }

    /**
     * Sort array in place
     * @param objects the array to sort
     * @param begin index of the first element in array - parallel: STL collection's begin()
     * @param end   index <b>after</b> the last element in array - parallel: STL collection's end()
     */
    void sort(T[] objects,int begin,int end);
}
