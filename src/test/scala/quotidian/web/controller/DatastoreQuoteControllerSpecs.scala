package quotidian.web.controller

import quotidian.DatastoreSpecification
import quotidian.model.Quote

object DatastoreQuoteControllerSpecs extends DatastoreSpecification {
	val controller = new DatastoreQuoteController
	"A controller" should {
		val quote = Quote("text","source","context")
		"save a quote" >> {
			val key = controller.save(quote)
			key must notBeNull
		}
	}
}
