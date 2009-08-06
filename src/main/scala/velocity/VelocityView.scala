package velocity

import java.util.{ArrayList,List => JList,Properties}
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import scala.collection.jcl.Conversions.unconvertList
import org.apache.velocity.{Template,VelocityContext}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.context.Context

object VelocityHelper {
	private[this] val properties = new Properties()
	properties.load(getClass().getClassLoader().getResourceAsStream("velocity/velocity.properties"))
	private[this] val engine = new VelocityEngine(properties)
	def getTemplate(template:String) = engine.getTemplate(template)
}

private[velocity] class IterableWrapper[T](iterable:Iterable[T]) extends java.lang.Iterable[T] {
	def iterator() = new java.util.Iterator[T] {
		private val delegate = iterable.elements
		def hasNext = delegate.hasNext
		def next() = delegate.next
		def remove = {
			throw new UnsupportedOperationException
		}
	}
}

class VelocityView(path:String) {
	val template:Template = VelocityHelper.getTemplate(path)
	def createVelocityContext(model:Map[String,Any],request:Request,response:Response) = {
		val context = new VelocityContext()
		model.foreach(t => {
			if (t._2.isInstanceOf[Iterable[_]])
				context.put(t._1, new IterableWrapper(t._2.asInstanceOf[Iterable[_]]))
			else
				context.put(t._1, t._2)
		})
		context
	}
	def render(model:Map[String,Any],request:Request,response:Response):Unit = {
		val context = createVelocityContext(model,request,response)
		response.setCharacterEncoding("UTF-8")
		template.merge(context,response.getWriter())
	}
}
