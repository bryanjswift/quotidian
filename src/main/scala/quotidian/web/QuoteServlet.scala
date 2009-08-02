package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.model.Quote
import quotidian.web.controller.QuoteController
import velocity.{VelocityHelper,VelocityView}

class QuoteServlet extends HttpServlet {
	override def doGet(request:Request, response:Response) {
		val view = new VelocityView("templates/default.vm")
		view.render(Map("quotes" -> QuoteServlet.controller.all),request,response)
	}
	override def doPost(request:Request, response:Response) {
		val text = request.getParameterValues("text")(0)
		val source = request.getParameterValues("source")(0)
		val context = request.getParameterValues("context")(0)
		val quote = new Quote(text,source,context)
		QuoteServlet.controller.save(quote)
		response.sendRedirect("/quote")
	}
}

object QuoteServlet {
	val controller = ConfigFactory.objectForProperty("quote.controller").asInstanceOf[QuoteController]
}
