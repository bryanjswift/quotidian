package quotidian.web

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import quotidian.Config
import quotidian.model.Quote
import quotidian.web.controller.{QuoteController,SearchController}
import velocity.{VelocityHelper,VelocityView}

class SearchServlet extends HttpServlet {
}

object SearchServlet {
	val qc = Config.objectForProperty[QuoteController]("quote.controller")
	val sc = Config.objectForProperty[SearchController]("search.controller")
}
