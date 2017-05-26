package murphytalk.algo

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

/**
 * Created by murphytalk on 5/22/2017.
 */
class TestShuntingYard{
    @Test fun testRPN() {
        assertThat(RPN("512+4*+3-"), `is`(14))
    }

    @Test(expected=RuntimeException::class) fun testRPN_InvalidOperator(){
        RPN("512D34")
    }
}