package murphytalk.arithmetic
import org.fusesource.jansi.Ansi.Color.*
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole

class Arithmetic{
    fun generate():String {
        return ""
    }
}

fun main(args:Array<String>){
    AnsiConsole.systemInstall();

    println(ansi().eraseLine().fg(RED).a("TEST ").fg(GREEN).a(" BEGIN!"))

    println(ansi().fg(WHITE).a("END"))
    AnsiConsole.systemUninstall();
}
