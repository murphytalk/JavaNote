package murphytalk.algo

import com.natpryce.hamkrest.assertion.assert // https://github.com/npryce/hamkrest
import com.natpryce.hamkrest.equalTo
import org.junit.Test

/**
 * Created by murphytalk on 5/22/2017.
 */
class TestShuntingYard{
    @Test fun testRPN() {
        assert.that(RPN("512+4*+3-"), equalTo(14))
    }

    @Test(expected=RuntimeException::class) fun testRPN_InvalidOperator(){
        RPN("512D34")
    }
}