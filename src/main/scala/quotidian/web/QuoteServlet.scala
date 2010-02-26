package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.Config
import quotidian.model.Quote
import quotidian.web.controller.{QuoteController,SearchController}
import velocity.{VelocityHelper,VelocityView}

class QuoteServlet extends HttpServlet {
	val Labels = Config.Labels
	override def doGet(request:Request, response:Response) = doGet(request,response,Nil)
	def doGet(request:Request,response:Response,errors:List[String]) = {
		val view = new VelocityView("templates/default.vm")
		view.render(Map("quotes" -> Config.qc.page(1),"errors" -> errors) ++ Labels,request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val text = http(Quote.Text,Labels(Quote.Text))
		val source = http(Quote.Source,Labels(Quote.Source))
		val context = http(Quote.Context,Labels(Quote.Context))
		var errors:List[String] = Nil
		try {
			val quote = Quote(text.get,source.getOrElse(""),context.getOrElse(""))
			Config.qc.save(quote)
		} catch {
			case nsee:NoSuchElementException =>
				errors = "\"" + Labels(Quote.Text) + "\" is required." :: errors
		}
		if (errors.length > 0) { doGet(request,response,errors) }
		else { response.sendRedirect("/") }
	}
}

