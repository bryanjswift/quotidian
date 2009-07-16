package quotidian.web

import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}

class QuoteServlet extends HttpServlet {
	override def doGet(request:Request, response:Response) {
		response.sendRedirect("/index.jsp")
	}
	override def doPost(request:Request, response:Response) {
		val text = request.getParameterValues("text")(0)
		val source = request.getParameterValues("source")(0)
		val context = request.getParameterValues("context")(0)
		val quote = new Quote(text,source,context)
		DatastorePersister.save(quote)
		response.sendRedirect("/quote")
	}
}
