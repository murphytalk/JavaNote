package murphytalk.web
import com.hubspot.jinjava.JinjavaConfig
import com.hubspot.jinjava.loader.ClasspathResourceLocator
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine
import com.google.gson.Gson
import murphytalk.arithmetic.ArithmeticGenerator
import murphytalk.arithmetic.evalArithmetic


object QuestionMaster{
    data class Question (val question:String, val correctAnswer:Int, var answered:Boolean = false)
    //todo : load parameters from serialization
    var generator = ArithmeticGenerator(4)
    fun generate() : Array<Question>{
        return Array(10, { _ -> val s = generator.generate(); Question(s, evalArithmetic(s)) })
    }
}

fun main(args:Array<String>){
    val logger = LoggerFactory.getLogger("murphytalk.web")
    val gson = Gson()
    logger.info("start")
    staticFiles.location("/static")

    val templateEngine = JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator())
    val commonAttributes = hashMapOf(
                "title" to "Math Homework" ,
                "year" to 2018
    )

    get("/math", { _, _ -> ModelAndView(commonAttributes, "templates/math.jinja2") }, templateEngine)
    get("/math/questions", { _, response ->
        response.status(200)
        response.type("application/json")
        gson.toJson(QuestionMaster.generate())
    })
}
