package murphytalk;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MiscJava {
    @Test
    public void testLocalDate(){
        final var f1 = DateTimeFormatter.ofPattern("MM/yyyy dd");
        assertThat(LocalDate.parse("03/2020 01", f1), is(LocalDate.of(2020,3,1)));
    }
}
