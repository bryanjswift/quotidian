package quotidian.web.controller

import org.apache.lucene.index.IndexReader
import quotidian.DatastoreSpecification
import quotidian.model.Quote

object DatastoreQuoteControllerSpecs extends DatastoreSpecification {
	val controller = new DatastoreQuoteController
	"A controller" should {
		datastoreCleanup.before
		val quote = Quote("this is some text","this is a source","and this is context")
		"save a quote" >> {
			val key = controller.save(quote)
			key must notBeNull
		}
		"create files when saving a quote" >> {
			controller.save(quote)
			controller.directory.list.length must beGreaterThan(0)
		}
		"have a searcher with a reader with terms" >> {
			controller.save(quote)
			val terms = controller.searcher.getIndexReader.terms
			terms.next mustEqual true
		}
	}
}
