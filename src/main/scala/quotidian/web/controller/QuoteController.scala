package quotidian.web.controller

import basic.persistence.{Persister,Savable}
import java.io.Serializable
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.{IndexWriter,Term}
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.search.{FuzzyQuery,IndexSearcher,TermQuery}
import org.apache.lucene.store.Directory
import quotidian.Logging
import quotidian.model.Quote

abstract class QuoteController extends Logging {
	protected def persister:Persister
	protected def directory:Directory
	protected def writer:IndexWriter = new IndexWriter(directory,new StandardAnalyzer(),UNLIMITED)
	def searcher = new IndexSearcher(directory)
	private lazy val textTerm = new Term(Quote.Text)
	private lazy val sourceTerm = new Term(Quote.Source)
	private lazy val contextTerm = new Term(Quote.Context)
	def all:List[Quote] = savablesToQuotes(persister.all(Quote.Kind))
	def bySource(source:String):Array[Quote] = {
		val s = searcher
		val scoreDocs = s.search(new FuzzyQuery(sourceTerm.createTerm(source)),10).scoreDocs
		for {
			scoreDoc <- scoreDocs
			doc = new DocumentWrapper(s.doc(scoreDoc.doc))
		} yield Quote(doc(Quote.Id).get,doc(Quote.Text).get,doc(Quote.Source).getOrElse(""),doc(Quote.Context).getOrElse(""))
	}
	def get(id:Serializable):Quote = persister.get(Quote.Kind,id).asInstanceOf[Quote]
	def random:Quote = {
		val quotes = all
		val position = (quotes.length * Math.random).toInt
		quotes(position)
	}
	private def savablesToQuotes(savables:List[Savable]):List[Quote] = {
		for {
			savable <- savables
		} yield savable.asInstanceOf[Quote]
	}
	def save(quote:Quote):Serializable = {
		val key = persister.save(quote)
		val w = writer
		try {
			w.addDocument(quote)
			w.commit
		} finally {
			w.close
		}
		key
	}
}

