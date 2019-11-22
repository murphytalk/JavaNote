package murphytalk.arithmetic

import org.fusesource.jansi.Ansi.Color.*
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import java.util.*

private fun waitCommand():String{
    val scanner = Scanner(System.`in`)
    return scanner.next()
}

class ArithmeticConsoleApp(generator:ArithmeticGenerator, private val num:Int){
    data class Question (val question:String, val correctAnswer:Int, var answered:Boolean = false, var userAnswer:Int = 0)
    // array of generated questions
    private val questions = Array(num) { _ -> val s = generator.generate(); Question(s, evalArithmetic(s)) }

    fun run() {
        var highlightWrong = false
        while(true) {
            if(list(highlightWrong) == num) break
            val cmd = waitCommand()
            if (cmd[0] in '0'..'9') {
                val idx = cmd[0]-'0'
                var wait_for_answer : Boolean
                do{
                    println(ansi().eraseLine().fg(WHITE).a("Input answer for question #$idx"))
                    try {
                        val answer = waitCommand().toInt()
                        questions[idx].answered = true
                        questions[idx].userAnswer = answer
                        wait_for_answer = false
                    }catch (e:Exception){
                        wait_for_answer = true
                    }
                }while(wait_for_answer)
            } else {
                //check answer
                highlightWrong  = true
            }
        }
    }

    //return the number of correct answered questions
    private fun list(hightlightWrongAnswer:Boolean = false): Int{
        var i = 0
        var correct = 0
        questions.map { (question, correctAnswer, answered, userAnswer) ->
            print(ansi().fg(WHITE).a("#$i ").fg(YELLOW).a(question).fg(WHITE).a(" = "))
            if(answered){
                if(correctAnswer == userAnswer) ++correct
                if(hightlightWrongAnswer && correctAnswer!=userAnswer){
                    print(ansi().fg(RED))
                }
                else{
                    print(ansi().fg(BLUE))
                }
                println(ansi().a(userAnswer))
            }
            else println(ansi().a("?"))
            ++i
        }
        println()
        println(ansi().fg(WHITE).a("Input [").fg(RED).a("0-${num-1}").fg(WHITE).a("] to answer a question or any other key to check answer"))
        return correct
    }
}

fun main(args:Array<String>){
    var s = if(args.size == 1) args[0] else "10" //default is 10
    var num : Int
    while(true){
        num = try {
            s.toInt()
        }
        catch(e: NumberFormatException){
            0
        }
        if(num <1 || num>10){
            println("Need a number between 1 and 10")
            s = waitCommand()
        }
        else break
    }

    AnsiConsole.systemInstall()

    println(ansi().eraseScreen().fg(GREEN).a("Hi buddy, let's do some math!"))
    println()

    val generator = ArithmeticGenerator()
    val arithmetic = ArithmeticConsoleApp(generator, num)

    arithmetic.run()

    println()
    println(ansi().fg(WHITE).a("See you next time !"))

    AnsiConsole.systemUninstall()
}
