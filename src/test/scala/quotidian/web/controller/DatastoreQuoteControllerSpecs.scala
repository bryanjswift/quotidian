package quotidian.web.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{IndexReader,IndexWriter,Term}
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.search.{FuzzyQuery,IndexSearcher,TermQuery}
import org.apache.lucene.util.Version
import quotidian.DatastoreSpecification
import quotidian.model.Quote
import com.bryanjswift.lucene.store.gae.DatastoreDirectory

class DatastoreControllerSpecs extends DatastoreSpecification {
	val quoteController = new DatastoreQuoteController
	val searchController = new DatastoreSearchController
	val q1 = Quote("I like oysters but they never make me think 'I want dick in me'.","Mary Lee","A barbeque after some drinks")
	val q2 = Quote("You should eat more vegetables","Jeremy Swift","In regards to my dark circles")
	val q3 = Quote("But I need you desperately","","Entourage theme song")
	"A DatastoreDirectory" should {
		datastoreCleanup.after
		"be able to have Quotes written to it" >> {
			val directory = new DatastoreDirectory
			val writer = new IndexWriter(directory,new StandardAnalyzer(Version.LUCENE_30),UNLIMITED)
			writer.addDocument(q1)
			writer.commit
			val searcher = new IndexSearcher(directory)
			val reader = searcher.getIndexReader
			val terms = reader.terms
			terms.next mustEqual true
			reader.close
		}
		"be able to find Quotes written to it" >> {
			val directory = new DatastoreDirectory()
			val writer = new IndexWriter(directory,new StandardAnalyzer(Version.LUCENE_30),UNLIMITED)
			writer.addDocument(q1)
			writer.commit
			val searcher = new IndexSearcher(directory)
			val scoreDocs = searcher.search(new FuzzyQuery(new Term(Quote.Source,"Mary")),10).scoreDocs
			scoreDocs.length must beGreaterThan(0)
		}
	}
	"A controller" should {
		datastoreCleanup.after
		"save a quote" >> {
			val key = quoteController.saveAndIndex(q1)
			key must notBeNull
		}
		"have a searcher with a reader with terms" >> {
			quoteController.saveAndIndex(q1)
			val terms = searchController.searcher.getIndexReader.terms
			terms.next mustEqual true
		}
		"find Quotes by source" >> {
			quoteController.saveAndIndex(q1)
			quoteController.saveAndIndex(q2)
			quoteController.saveAndIndex(q3)
			searchController.searchSource("Mary").length must beGreaterThan(0)
			searchController.search("(source:Mary Lee)").length must beGreaterThan(0)
			searchController.searchSource("Mary Lee").length must beGreaterThan(0)
		}
		"find Quotes by context" >> {
			quoteController.saveAndIndex(q1)
			searchController.searchContext("barbeque").length must beGreaterThan(0)
		}
		"find Quotes by text" >> {
			quoteController.saveAndIndex(q1)
			searchController.searchText("dick").length must beGreaterThan(0)
		}
	}
	"Saving fifty (50) Quotes" should {
		datastoreCleanup.after
		"not throw an error" >> {
			val key = quoteController.saveAndIndex(q1)
			quoteController.saveAndIndex(q2)
			quoteController.saveAndIndex(q3)
			for (i <- 0 to 50) {
				quoteController.saveAndIndex(Quote("This is a test","Test source","test context"))
			}
			quoteController.get(key) must_== q1
		}
	}
}
