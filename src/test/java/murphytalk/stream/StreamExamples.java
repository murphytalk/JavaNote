package murphytalk.stream;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import murphytalk.utils.Files;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class StreamExamples {
    private static class MyClass{
        public final String s;
        public final int i;
        public final double d;
        public MyClass(String s, int i, double d) {
            this.s = s;
            this.i = i;
            this.d = d;
        }

        @Override
        public boolean equals(Object obj) {
            return Optional.ofNullable(obj)
                    .filter(that -> that instanceof MyClass )
                    .map( that -> (MyClass)that)
                    .filter( that -> Objects.equals(this.s,that.s))
                    .filter( that -> this.i == that.i)
                    .filter( that -> this.d == that.d)
                    .isPresent();
        }
    }

    @Test
    public void testOptionalEqualsNull(){
        MyClass m1 = new MyClass("abc",1,1.d);
        MyClass m2 = null;
        assertFalse(m1.equals(m2));
    }

    @Test
    public void testOptionalEqualsNullStr(){
        MyClass m1 = new MyClass("abc",1,1.d);
        MyClass m2 = new MyClass(null,1,1.d);
        assertFalse(m1.equals(m2));
    }

    @Test
    public void testOptionalEquals(){
        MyClass m1 = new MyClass("abc",1,1.d);
        MyClass m2 = new MyClass("abc",1,1.d);
        assertTrue(m1.equals(m2));
    }

     @Test
     public void testOptionalEqualsNeqStr(){
        MyClass m1 = new MyClass("abc",1,1.d);
        MyClass m2 = new MyClass("123",1,1.d);
        assertFalse(m1.equals(m2));
    }

    @Test
    public void top20Frequency() throws URISyntaxException {
        Files.readTextFileInZip( Files.getFileFromClassPath(this.getClass(),"/test-data.zip"), "bible.txt", ss ->
            ss.flatMap(line -> Arrays.asList(line.split("\\b")).stream())  //collapse from stream of stream of string to stream of string
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry<String,Long>::getValue).reversed())
                    .limit(20)
                    .map(Map.Entry::getKey)
                    //.collect(Collectors.toList());
                    .forEach( s -> System.out.println(s))
        );
    }
}
