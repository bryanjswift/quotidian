package velocity

import java.io.{PrintWriter,StringWriter}
import org.specs.Specification

object VelocityViewSpecs extends Specification {
	class Request extends DummyRequest { }
	class Response extends DummyResponse {
		val writer = new StringWriter()
		override def getWriter():PrintWriter = new PrintWriter(writer)
    override def setCharacterEncoding(encoding:String) = { }
	}
	val objTestPath = "templates/objTest.vm"
	val listTestPath = "templates/listTest.vm"

	def have = addToSusVerb("have")
	"Creating a VelocityView with a valid template path" should {
		val view = new VelocityView(objTestPath)
		"produce a view with a non-null template" in {
			view.template must notBeNull
		}
		"produce a non-empty context when passed a valid model" in {
			val context = view.createVelocityContext(Map("tests" -> List("list test")),null,null)
			val keys = context.getKeys
			keys.length mustEqual 1
		}
	}
	"When rendering a view (" + objTestPath + ") with a simple object (obj), the view" should {
		val view = new VelocityView(objTestPath)
		val request = new Request()
		val response = new Response()
		val obj = new Object {
			val test = "Tester.test"
			override def toString = "Tester.toString"
		}
		view.render(Map("obj" -> obj),request,response)
		val result = response.writer.toString
		"contain the value of obj.toString" in {
			result indexOf obj.toString must beGreaterThanOrEqualTo(0)
		}
		"contain the value of obj.test" in {
			result indexOf obj.test must beGreaterThanOrEqualTo(0)
		}
	}
	"When rendering a view (" + listTestPath + ") with a model containing a list, the view" should {
		val view = new VelocityView(listTestPath)
		val request = new Request()
		val response = new Response()
		val list = List("list test","list test2","another test")
		view.render(Map("tests" -> list),request,response)
		val result = response.writer.toString
		"contain the first string" in {
			result must notEqualIgnoreSpace("")
			result indexOf list(0) must beGreaterThanOrEqualTo(0)
		}
		"contain all of the strings" in {
			result must notEqualIgnoreSpace("")
			list.foreach(s => {
				result indexOf s must beGreaterThanOrEqualTo(0)
			})
		}
	}
}
