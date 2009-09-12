package quotidian.web.resources

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{GET,Produces,Path,PathParam}
import javax.ws.rs.core.MediaType.{APPLICATION_XML,TEXT_PLAIN,TEXT_XML}
import quotidian.web.controller.{QuoteController,SearchController}

@Singleton
@Path("/quotes")
class QuoteResource {
	import QuoteResource._
	@GET
	@Path("/single/{id}")
	@Produces(Array(APPLICATION_XML,TEXT_PLAIN,TEXT_XML))
	def getQuote(@PathParam("id") sid:String):String = {
		val xml = if (isLong(sid)) {
			try {
				qc.get(sid.toLong).asXml
			} catch {
				case ex:Exception =>
					<error><message>{ex.getMessage}</message></error>
			}
		} else {
			<error><message>Quotes must have numeric ids</message></error>
		}
		xml.toString
	}
}

object QuoteResource {
	val qc = ConfigFactory.objectForProperty[QuoteController]("quote.controller")
	val sc = ConfigFactory.objectForProperty[SearchController]("search.controller")
	private def isLong(s:String) = s.matches("\\d+")
}
