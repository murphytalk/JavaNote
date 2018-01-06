package murphytalk.web
import spark.Spark.*
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine
import java.util.HashMap



fun main(args:Array<String>){
    staticFiles.location("/static")
    get("/math", { request, response ->
        val attributes = HashMap<String, Any>()
        //attributes.put("message", "spark-template-jinjava")
        val md = ModelAndView(attributes, "templates/math.jinja2")
        val e = JinjavaEngine()
        val s = e.render(md)
        val a = 0
        s
    })
}
