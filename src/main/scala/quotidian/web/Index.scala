package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}

class Index extends HttpServlet {
	override def doGet(request:Request, response:Response) {
		response.sendRedirect("/index.jsp")
	}
}
