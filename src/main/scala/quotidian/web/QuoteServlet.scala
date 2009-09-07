package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.model.Quote
import quotidian.web.controller.{QuoteController,SearchController}
import velocity.{VelocityHelper,VelocityView}

class QuoteServlet extends HttpServlet {
	override def doGet(request:Request, response:Response) {
		val view = new VelocityView("templates/default.vm")
		view.render(Map("quotes" -> QuoteServlet.qc.all),request,response)
	}
	override def doPost(request:Request, response:Response) {
		val text = request.getParameterValues(Quote.Text)(0)
		val source = request.getParameterValues(Quote.Source)(0)
		val context = request.getParameterValues(Quote.Context)(0)
		val quote = new Quote(text,source,context)
		QuoteServlet.qc.save(quote)
		response.sendRedirect("/quote")
	}
}

object QuoteServlet {
	val qc = ConfigFactory.objectForProperty[QuoteController]("quote.controller")
	val sc = ConfigFactory.objectForProperty[SearchController]("search.controller")
}
