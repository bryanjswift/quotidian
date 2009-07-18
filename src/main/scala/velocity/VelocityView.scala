package velocity

import java.util.Properties
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.velocity.{Template,VelocityContext}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.context.Context

object VelocityHelper {
	private class Props extends Properties {
		def setProperties(properties:Map[String,String]) = {
			properties.foreach(prop => setProperty(prop._1,prop._2))
			this
		}
	}
	private[this] val properties = new Props().setProperties(Map(
		"resource.loader" -> "class",
		"class.resource.loader.class" -> "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader",
		"class.resource.loader.cache" -> "true"
	))
	private[this] val engine = new VelocityEngine(properties)
	def getTemplate(template:String) = engine.getTemplate(template)
}

class VelocityView(template:Template) {
	def createVelocityContext(model:Map[String,Any],request:Request,response:Response) = {
		val context = new VelocityContext()
		model.foreach(t => context.put(t._1, t._2))
		context
	}
	def render(model:Map[String,Any],request:Request,response:Response):Unit = {
		val context = createVelocityContext(model,request,response)
		template.merge(context,response.getWriter())
	}
}