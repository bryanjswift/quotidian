package quotidian.web.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{IndexReader,IndexWriter,Term}
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.search.{FuzzyQuery,IndexSearcher,TermQuery}
import quotidian.DatastoreSpecification
import quotidian.model.Quote
import quotidian.search.DatastoreDirectory

object DatastoreQuoteControllerSpecs extends DatastoreSpecification {
	val controller = new DatastoreQuoteController
	val quote = Quote("this is some text","Bryan","and this is context")
	"A DatastoreDirectory" should {
		datastoreCleanup.after
		"be able to have Quotes written to it" >> {
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
		"be able to find Quotes written to it" >> {
			val directory = new DatastoreDirectory()
			val writer = new IndexWriter(directory,new StandardAnalyzer(),UNLIMITED)
			writer.addDocument(quote)
			writer.commit
			val searcher = new IndexSearcher(directory)
			val sourceTerm = new Term(Quote.Source)
			val scoreDocs = searcher.search(new FuzzyQuery(sourceTerm.createTerm("Bryan")),10).scoreDocs
			val quotes = for {
				scoreDoc <- scoreDocs
				doc = searcher.doc(scoreDoc.doc)
			} yield Quote(doc.getValues(Quote.Text)(0),doc.getValues(Quote.Source)(0),doc.getValues(Quote.Context)(0))
			quotes.length must beGreaterThan(0)
		}
	}
	"A controller" should {
		datastoreCleanup.after
		"save a quote" >> {
			val key = controller.save(quote)
			key must notBeNull
		}
		"have a searcher with a reader with terms" >> {
			controller.save(quote)
			val terms = controller.searcher.getIndexReader.terms
			terms.next mustEqual true
		}
		"find Quotes by source" >> {
			val key = controller.save(quote)
			val quotes = controller.bySource("Bryan")
			quotes.length must beGreaterThan(0)
		}
	}
}
