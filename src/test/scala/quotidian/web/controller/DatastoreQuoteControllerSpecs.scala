package quotidian.web.controller

import org.apache.lucene.index.IndexReader
import quotidian.DatastoreSpecification
import quotidian.model.Quote

object DatastoreQuoteControllerSpecs extends DatastoreSpecification {
	val controller = new DatastoreQuoteController
	"A controller" should {
		datastoreCleanup.before
		val quote = Quote("text","source","context")
		"save a quote" >> {
			val key = controller.save(quote)
			key must notBeNull
		}
		"have a searcher with at least one doc after a save" >> {
			controller.save(quote)
			controller.directory.list.length must beGreaterThanOrEqualTo(1)
		}
		"have a searcher which can find a doc" >> {
			controller.save(quote)
			controller.save(quote)
			controller.save(quote)
			controller.save(quote)
			controller.bySource("source").length must beGreaterThan(0)
		}
	}
}
