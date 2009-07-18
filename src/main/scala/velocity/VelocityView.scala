package velocity

import java.util.Properties
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.velocity.{Template,VelocityContext}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.context.Context

object VelocityHelper {
	private[this] val properties = new Properties()
	properties.load(getClass().getClassLoader().getResourceAsStream("velocity/velocity.properties"))
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