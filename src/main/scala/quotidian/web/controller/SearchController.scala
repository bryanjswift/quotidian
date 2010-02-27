package quotidian.web.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.{IndexSearcher,Query}
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version
import quotidian.Logging
import quotidian.model.Quote

abstract class SearchController extends Logging {
	protected def directory:Directory
	def searcher = new IndexSearcher(directory)
	def searchContext(context:String):Array[Quote] = search(Quote.Context + ":" + context)
	def searchSource(source:String):Array[Quote] = search(Quote.Source + ":" + source)
	def searchText(text:String):Array[Quote] = search(Quote.Text + ":" + text)
	def search(query:String):Array[Quote] = {
		val parser = new QueryParser(Version.LUCENE_30,Quote.All,new StandardAnalyzer(Version.LUCENE_30))
		search(parser.parse(query))
	}
	private def search(query:Query):Array[Quote] = {
		val s = searcher
		val scoreDocs = s.search(query,10).scoreDocs
		for {
			scoreDoc <- scoreDocs
			doc = DocumentWrapper(s.doc(scoreDoc.doc))
		} yield Quote(doc(Quote.Id).get,doc(Quote.Text).get,doc(Quote.Source).getOrElse(""),doc(Quote.Context).getOrElse(""))
	}
}
