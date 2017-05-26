package murphytalk.algo

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

/**
 * Created by murphytalk on 5/22/2017.
 */
class TestShuntingYard{
    @Test fun testRPN1() {
        assertThat(RPN("5 1 2+4*+3-"), `is`(14))
    }

    @Test fun testRPN2() {
        assertThat(RPN("25 50+"), `is`(75))
    }

    @Test fun testRPN3(){
        assertThat(RPN("4 5^"), `is`(1024))
    }

    @Test(expected=RuntimeException::class) fun testRPN_InvalidOperator(){
        RPN("512D34")
    }
}