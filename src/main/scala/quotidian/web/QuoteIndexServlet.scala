package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.{Config,Logging}
import quotidian.model.Quote

class QuoteIndexServlet extends HttpServlet with Logging {
	val Labels = Config.Labels
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val id = http(Quote.Id)
		val quote = Config.qc.get(id.get)
		info("Processing index task for " + quote.toString)
		try {
			Config.qc.index(quote)
			info("Indexed " + quote.toString + " successfully")
			response.setStatus(Response.SC_OK)
		} catch {
			case e => {
				warn("Failed to index " + quote.toString)
				response.setStatus(Response.SC_INTERNAL_SERVER_ERROR)
			}
		}
	}
}
