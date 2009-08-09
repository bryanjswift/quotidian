package quotidian.web.controller

import basic.persistence.{Persister,Savable}
import java.io.Serializable
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Document,Field}
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.store.Directory
import quotidian.Logging
import quotidian.model.Quote
import QuoteController.quote2document

abstract class QuoteController extends Logging {
	def persister:Persister
	def directory:Directory
	lazy val indexWriter = new IndexWriter(directory,new StandardAnalyzer(),false,UNLIMITED)
	def all:List[Quote] = savablesToQuotes(persister.all(Quote.kind))
	def bySource(source:String):List[Quote] = savablesToQuotes(persister.search(Quote.kind,"source",source))
	def get(id:Serializable):Quote = persister.get(Quote.kind,id).asInstanceOf[Quote]
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
		indexWriter.addDocument(quote)
		indexWriter.commit
		key
	}
}

object QuoteController {
	implicit def quote2document(quote:Quote):Document = {
		val document = new Document()
		val text = new Field("text",quote.text,Field.Store.YES,Field.Index.ANALYZED)
		val source = new Field("source",quote.source,Field.Store.YES,Field.Index.ANALYZED)
		val context = new Field("context",quote.context,Field.Store.YES,Field.Index.ANALYZED)
		document.add(text)
		document.add(source)
		document.add(context)
		document
	}
}
