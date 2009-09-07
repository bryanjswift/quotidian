package quotidian.web.controller

import basic.persistence.Persister
import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.{FuzzyQuery,IndexSearcher,Query,TermQuery}
import org.apache.lucene.store.Directory
import quotidian.Logging
import quotidian.model.Quote

abstract class SearchController extends Logging {
	protected def persister:Persister
	protected def directory:Directory
	def searcher = new IndexSearcher(directory)
	private lazy val allTerm = new Term(Quote.All)
	private lazy val contextTerm = new Term(Quote.Context)
	private lazy val sourceTerm = new Term(Quote.Source)
	private lazy val textTerm = new Term(Quote.Text)
	def searchAll(s:String):Array[Quote] = search(new TermQuery(allTerm.createTerm(s)))
	def searchContext(context:String):Array[Quote] = search(new TermQuery(contextTerm.createTerm(context)))
	def searchSource(source:String):Array[Quote] = search(new TermQuery(sourceTerm.createTerm(source)))
	def searchText(text:String):Array[Quote] = search(new TermQuery(textTerm.createTerm(text)))
	private def search(query:Query):Array[Quote] = {
		val s = searcher
		val scoreDocs = s.search(query,10).scoreDocs
		for {
			scoreDoc <- scoreDocs
			doc = DocumentWrapper(s.doc(scoreDoc.doc))
		} yield Quote(doc(Quote.Id).get,doc(Quote.Text).get,doc(Quote.Source).getOrElse(""),doc(Quote.Context).getOrElse(""))
	}
}
