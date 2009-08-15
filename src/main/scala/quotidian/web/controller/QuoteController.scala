package quotidian.web.controller

import basic.persistence.{Persister,Savable}
import java.io.Serializable
import org.apache.lucene.index.{IndexWriter,Term}
import org.apache.lucene.search.{FuzzyQuery,IndexSearcher,TermQuery}
import org.apache.lucene.store.Directory
import quotidian.Logging
import quotidian.model.Quote

abstract class QuoteController extends Logging {
	def persister:Persister
	def directory:Directory
	def writer:IndexWriter
	lazy val searcher = new IndexSearcher(directory)
	lazy val textTerm = new Term(Quote.Text)
	lazy val sourceTerm = new Term(Quote.Source)
	lazy val contextTerm = new Term(Quote.Context)
	def all:List[Quote] = savablesToQuotes(persister.all(Quote.Kind))
	def bySource(source:String):Array[Quote] = {
		val scoreDocs = searcher.search(new FuzzyQuery(sourceTerm.createTerm(source)),10).scoreDocs
		for {
			scoreDoc <- scoreDocs
			doc = searcher.doc(scoreDoc.doc)
		} yield Quote(doc.getValues(Quote.Text)(0),doc.getValues(Quote.Source)(0),doc.getValues(Quote.Context)(0))
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
		writer.addDocument(quote)
		writer.commit
		key
	}
}

