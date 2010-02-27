package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.{Config,Logging}
import quotidian.model.Search
import quotidian.web.controller.{QuoteController,SearchController}
import velocity.{VelocityHelper,VelocityView}

class SearchServlet extends HttpServlet with Logging {
	val Labels = Config.Labels
	override def doGet(request:Request, response:Response) { doSearch(request,response) }
	override def doPost(request:Request, response:Response) { doSearch(request,response) }
	def doSearch(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val q = http(Search.Key,Labels(Search.Key))
		val context = q match {
			case None =>
				Map("quotes" -> Nil,"searchError" -> Labels(Search.Empty),"query" -> q)
			case Some(query) =>
				Map("quotes" -> Config.sc.search(query),"searchError" -> "","query" -> query)
		}
		val view = new VelocityView("templates/search.vm")
		view.render(context ++ Labels,request,response)
	}
}

