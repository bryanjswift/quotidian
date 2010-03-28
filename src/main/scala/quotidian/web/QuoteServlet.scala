package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.{Config,Logging}
import quotidian.model.Quote
import quotidian.web.controller.{QuoteController,SearchController}
import velocity.VelocityView

class QuoteServlet extends QcServlet with Logging {
	override lazy val html = "templates/default.vm"
	val Labels = Config.Labels

	override def processGet(http:HttpHelper) = processGet(http,Nil)
	def processGet(http:HttpHelper,errors:List[String]) =
		Map("quotes" -> Config.qc.page(1),"errors" -> errors) ++ Labels

	override def doPost(request:Request, response:Response) {
		val http = new QuoteServlet.this.HttpHelper(request,response)
		val text = http(Quote.Text,Labels(Quote.Text))
		val source = http(Quote.Source,Labels(Quote.Source))
		val context = http(Quote.Context,Labels(Quote.Context))
		var errors:List[String] = Nil
		try {
			val quote = Quote(text.get,source.getOrElse(""),context.getOrElse(""))
			val id = Config.qc.save(quote)
			// putting the quote in the task queue is done here because I couldn't
			// test QuoteController's save and queue functionality without having a
			// web server running to catch queue inserts (Thanks GAE)
			Config.qc.enqueue(id)
		} catch {
			case nsee:NoSuchElementException =>
				errors = "\"" + Labels(Quote.Text) + "\" is required." :: errors
		}
		if (errors.length > 0) { 
			val context = processGet(http,errors)
			val view = new VelocityView(http.view)
			view.render(context,request,response)
		} else { response.sendRedirect("/") }
	}
}

