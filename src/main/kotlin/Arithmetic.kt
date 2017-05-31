package murphytalk.arithmetic
import org.fusesource.jansi.Ansi.Color.*
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole

class Arithmetic(generator:ArithmeticGenerator, num:Int){
    // array of generated (question, answer) pairs
    val questions = Array(num, { _ -> val s = generator.generate(); Pair(s, evalArithmetic(s)) })
    // to save answers answered by user
    val answers = Array(num, { _ -> 0 })
}

fun main(args:Array<String>){
    AnsiConsole.systemInstall()

    println(ansi().eraseScreen().fg(GREEN).a("Hi buddy, let's do some math!"))
    println()

    val b = ArithmeticGenerator.Builder()
    val generator = b.build()

    val arithmetic = Arithmetic(generator, 10)

    arithmetic.questions.map { (question, answer) ->  println(ansi().fg(YELLOW).a(question).fg(WHITE).a(" = ").fg(BLUE).a(answer)) }


    println()
    println(ansi().fg(WHITE).a("See you next time !"))

    AnsiConsole.systemUninstall()
}
