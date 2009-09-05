package quotidian.web.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{IndexReader,IndexWriter}
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.search.IndexSearcher
import quotidian.DatastoreSpecification
import quotidian.model.Quote
import quotidian.search.DatastoreDirectory

object DatastoreQuoteControllerSpecs extends DatastoreSpecification {
	val controller = new DatastoreQuoteController
	"A DatastoreDirectory" should {
		datastoreCleanup.before
		"be able to have Quotes written to it" >> {
			val quote = Quote("this is some text","this is a source","and this is context")
			val directory = new DatastoreDirectory()
			val writer = new IndexWriter(directory,new StandardAnalyzer(),UNLIMITED)
			writer.addDocument(quote)
			writer.commit
			val searcher = new IndexSearcher(directory)
			val reader = searcher.getIndexReader
			val terms = reader.terms
			terms.next mustEqual true
			reader.close
		}
	}
	"A controller" should {
		datastoreCleanup.before
		val quote = Quote("this is some text","this is a source","and this is context")
		"save a quote" >> {
			val key = controller.save(quote)
			key must notBeNull
		}
		"have a searcher with a reader with terms" >> {
			controller.save(quote)
			val reader = controller.searcher.getIndexReader
			val terms = reader.terms
			terms.next mustEqual true
			reader.close
		}
	}
}
