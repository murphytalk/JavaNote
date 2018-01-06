package murphytalk.web
import com.hubspot.jinjava.JinjavaConfig
import com.hubspot.jinjava.loader.ClasspathResourceLocator
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine


fun main(args:Array<String>){
    val logger = LoggerFactory.getLogger("murphytalk.web")
    logger.info("start")
    staticFiles.location("/static")

    val templateEngine = JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator())
    val commonAttributes = hashMapOf(
                "title" to "Math Homework" ,
                "year" to 2018
    )
    get("/math", { _, _ -> ModelAndView(commonAttributes, "templates/math.jinja2") }, templateEngine)
}
