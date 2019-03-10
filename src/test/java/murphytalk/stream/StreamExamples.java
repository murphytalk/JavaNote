package murphytalk.stream;

import com.google.common.collect.Lists;
import murphytalk.test.StopWatch;
import murphytalk.utils.Files;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

//https://developer.ibm.com/articles/j-java-streams-3-brian-goetz/

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

        public Optional<String> svalue(){
            return Optional.ofNullable(s);
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
    public void testOptional(){
        {
            Optional<MyClass> m = Optional.empty();
            Optional<String> s = m.flatMap(MyClass::svalue);
            assertFalse(s.isPresent());
        }

        {
            Optional<MyClass> m = Optional.of(new MyClass("xyz", 1, 0));
            Optional<String> s = m.flatMap(MyClass::svalue);
            assertEquals(s.get(), "xyz");
        }

        Set<String> r = new HashSet<>();
        {
            MyClass e = new MyClass(null, 0, 1);
            e.svalue().ifPresent(r::add);
            assertTrue(r.isEmpty());
        }

        {
            MyClass e = new MyClass("abc", 0, 1);
            e.svalue().ifPresent(r::add);
            assertTrue(r.contains("abc"));
        }
    }

    @Test
    public void testCountryLangSets (){
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<String>> countryLanguageSets = locales.collect(
                Collectors.toMap(
                        Locale::getDisplayCountry,
                        l -> Collections.singleton(l.getDisplayLanguage()),
                        (a, b) -> {
                            // Union of a and b
                            Set<String> union = new HashSet<>(a);
                            union.addAll(b);
                            return union;
                        }
                        )
        );
        String[] lang = {"French", "German", "Italian"};
        assertTrue(countryLanguageSets.get("Switzerland").containsAll(Arrays.asList(lang)));
    }

    final Pattern word = Pattern.compile("\\w+");
    final Set<String> excludedWords = new HashSet<>(Arrays.asList("a", "the", "of", "and" ));
    private void getTopFrequentWords(Stream<String> words, int top, Consumer<String> onWord){
        words.flatMap(line -> Arrays.asList(line.split("\\b")).stream())  //collapse from stream of stream of string to stream of string
                    .map(s -> s.toLowerCase())
                    .filter( w -> word.matcher(w).matches())
                    .filter( w -> !excludedWords.contains(w))
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry<String,Long>::getValue).reversed())
                    .limit(top)
                    .map(Map.Entry::getKey)
                    //.collect(Collectors.toList())
                    .forEach( w -> onWord.accept(w));
    }

    @Test @Ignore
    public void top20FrequentWordsInBible() throws URISyntaxException {
        final File zip = Files.getFileFromClassPath(this.getClass(),"/test-data.zip");
        final List<String> a1 = Lists.newArrayList();
        final List<String> a2 = Lists.newArrayList();

        final long repeat = 100L;
        final int count = 20;


        StopWatch.measureSimple("Parallel",() -> {
                    a2.clear();
                    Files.readTextFileInZip(zip, "bible.txt", ss -> getTopFrequentWords(ss.parallel(), count, w -> a2.add(w)));
                },repeat);

        StopWatch.measureSimple("Sequential",() -> {
                    a1.clear();
                    Files.readTextFileInZip(zip, "bible.txt", ss -> getTopFrequentWords(ss, count, w -> a1.add(w)));
                },repeat);

        assertThat(a1,is(a2));

        a1.forEach( w-> System.out.println(w));
    }
}
