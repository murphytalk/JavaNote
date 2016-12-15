package murphytalk.algo;

public interface Sort {
    void sort(Comparable[] objects);
    /**
     * Sort array in place
     * @param objects the array to sort
     * @param begin index of the first element in array - parallel: STL collection's begin()
     * @param end   index <b>after</b> the last element in array - parallel: STL collection's end()
     */
    void sort(Comparable[] objects,int begin,int end);
}
