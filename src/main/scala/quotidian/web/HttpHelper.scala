package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.Logging

class HttpHelper(request:Request,response:Response) extends Logging {
	def apply(param:String,default:String = "") = {
		val value = request.getParameter(param)
		if (value == null || value == "" || value == default) None else Some(value)
	}
}

object HttpHelper {
	def apply(request:Request,response:Response) = new HttpHelper(request,response)
}
