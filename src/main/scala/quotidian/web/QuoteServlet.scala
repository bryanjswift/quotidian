package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.Config
import quotidian.model.Quote
import quotidian.web.controller.{QuoteController,SearchController}
import velocity.{VelocityHelper,VelocityView}

class QuoteServlet extends HttpServlet {
	val labels = Map(
		Quote.Text -> "*Words to remember...",
		Quote.Source -> "The wordsmith..",
		Quote.Context -> "Provide some context..")
	override def doGet(request:Request, response:Response) = doGet(request,response,Nil)
	def doGet(request:Request,response:Response,errors:List[String]) = {
		val view = new VelocityView("templates/default.vm")
		view.render(Map("quotes" -> QuoteServlet.qc.page(1),"errors" -> errors) ++ labels,request,response)
	}
	override def doPost(request:Request, response:Response) {
		def getParameter(param:String,default:String) = {
			val value = request.getParameter(param)
			if (value == "" || value == default) None else Some(value)
		}
		val text = getParameter(Quote.Text,labels(Quote.Text))
		val source = getParameter(Quote.Source,labels(Quote.Source))
		val context = getParameter(Quote.Context,labels(Quote.Context))
		var errors:List[String] = Nil
		try {
			val quote = new Quote(text.get,source.getOrElse(""),context.getOrElse(""))
			QuoteServlet.qc.save(quote)
		} catch {
			case nsee:NoSuchElementException =>
				errors = "\"" + labels(Quote.Text) + "\" is required." :: errors
		}
		if (errors.length > 0) { doGet(request,response,errors) }
		else { response.sendRedirect("/") }
	}
}

object QuoteServlet {
	val qc = Config.objectForProperty[QuoteController]("quote.controller")
	val sc = Config.objectForProperty[SearchController]("search.controller")
}
