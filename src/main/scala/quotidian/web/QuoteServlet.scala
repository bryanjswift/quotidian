package quotidian.web

import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister
import quotidian.web.controller.QuoteController
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import javax.servlet.jsp.{JspFactory,PageContext}

class QuoteServlet extends HttpServlet {
	val jspFactory = JspFactory.getDefaultFactory()
	override def doGet(request:Request, response:Response) {
		val pageContext = jspFactory.getPageContext(this,request,response,null,true,8192,true)
		pageContext.setAttribute("test","test string from servlet")
		pageContext.setAttribute("quotes",QuoteController.all)
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
