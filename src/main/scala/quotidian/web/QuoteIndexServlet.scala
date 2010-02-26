package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.{Config,Logging}
import quotidian.model.Quote

class QuoteIndexServlet extends HttpServlet with Logging {
	val Labels = Config.Labels
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val id = http(Quote.Id)
		val text = http(Quote.Text,Labels(Quote.Text))
		val source = http(Quote.Source,Labels(Quote.Source))
		val context = http(Quote.Context,Labels(Quote.Context))
		try {
			Config.qc.index(Quote(id.get,text.get,source.get,context.get))
			response.setStatus(Response.SC_OK)
		} catch {
			case e => response.setStatus(Response.SC_INTERNAL_SERVER_ERROR)
		}
	}
}
