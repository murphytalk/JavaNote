package murphytalk.algo;

public abstract class AbstractSort implements Sort{
    @Override
    public void sort(Comparable[] objects) {
        sort(objects,0,objects.length);
    }
}
