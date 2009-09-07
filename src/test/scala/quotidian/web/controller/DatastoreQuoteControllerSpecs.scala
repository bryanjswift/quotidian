package quotidian.web.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{IndexReader,IndexWriter,Term}
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.search.{FuzzyQuery,IndexSearcher,TermQuery}
import quotidian.DatastoreSpecification
import quotidian.model.Quote
import quotidian.search.DatastoreDirectory

object DatastoreControllerSpecs extends DatastoreSpecification {
	val quoteController = new DatastoreQuoteController
	val searchController = new DatastoreSearchController
	val quote = Quote("I like oysters but they never make me think 'I want dick in me'.","Mary Lee","A barbeque after some drinks")
	"A DatastoreDirectory" should {
		datastoreCleanup.after
		"be able to have Quotes written to it" >> {
			val directory = new DatastoreDirectory
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
			val scoreDocs = searcher.search(new TermQuery(new Term("source","Mary")),10).scoreDocs
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
			val key = quoteController.save(quote)
			key must notBeNull
		}
		"have a searcher with a reader with terms" >> {
			quoteController.save(quote)
			val terms = searchController.searcher.getIndexReader.terms
			terms.next mustEqual true
		}
		"find Quotes by source" >> {
			val key = quoteController.save(quote)
			val quotes = searchController.searchSource("Mary")
			quotes.length must beGreaterThan(0)
		}
		"find Quotes by context" >> {
			val key = quoteController.save(quote)
			val quotes = searchController.searchContext("barbeque")
			quotes.length must beGreaterThan(0)
		}
	}
}
