package velocity

import java.io.{PrintWriter,StringWriter}
import org.specs.Specification
import velocity.{MockRequest => Request, MockResponse => Response}

object VelocityViewSpecs extends Specification {
	class Tester {
		val test = "Tester.test"
		override def toString() = "Tester.toString"
	}
	class RequestMock extends Request { }
	class ResponseMock extends Response {
		val writer = new StringWriter()
		override def getWriter():PrintWriter = new PrintWriter(writer)
	}

	def have = addToSusVerb("have")
	"view.template" should {
		val view = new VelocityView("templates/listTest.vm")
		"not be null" in {
			view.template must notBeNull
		}
	}
	"view.createVelocityContext" should {
		val view = new VelocityView("templates/listTest.vm")
		"return a context with at least one value" in {
			val context = view.createVelocityContext(Map("tests" -> List("list test")),null,null)
			val keys = context.getKeys
			keys.length mustEqual 1
		}
	}
	"rendering with an object (obj)" should {
		val view = new VelocityView("templates/objTest.vm")
		val request = new RequestMock()
		val response = new ResponseMock()
		val obj = new Tester()
		view.render(Map("obj" -> obj),request,response)
		val result = response.writer.toString
		"contain obj.toString" in {
			result indexOf obj.toString must beGreaterThanOrEqualTo(0)
		}
		"contain obj.test" in {
			result indexOf obj.test must beGreaterThanOrEqualTo(0)
		}
	}
	"rendering with list of Strings" should {
		val view = new VelocityView("templates/listTest.vm")
		val request = new RequestMock()
		val response = new ResponseMock()
		val list = List("list test","list test2","another test")
		view.render(Map("tests" -> list),request,response)
		val result = response.writer.toString
		"contain one of the strings" in {
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
