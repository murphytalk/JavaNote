package murphytalk.algo

import com.natpryce.hamkrest.assertion.assert // https://github.com/npryce/hamkrest
import com.natpryce.hamkrest.equalTo
import org.junit.Test

/**
 * Created by murphytalk on 5/22/2017.
 */
class TestShuntingYard(){
    private var rpn = RPN()

    @Test fun testRPN() {
        val rpn = RPN()
        assert.that(rpn.evaluate("512+4*+3-"), equalTo(14))
    }

    @Test(expected=RuntimeException::class) fun testRPN_InvalidOperator(){
        rpn.evaluate("512D34")
    }
}