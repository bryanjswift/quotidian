package quotidian.web.controller

import basic.persistence.{Persister,Savable}
import java.io.Serializable
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.store.Directory
import quotidian.Logging
import quotidian.model.Quote

abstract class QuoteController extends Logging {
	def persister:Persister
	def directory:Directory
	lazy val indexWriter = new IndexWriter(directory,new StandardAnalyzer(),true,UNLIMITED)
	def all:List[Quote] = savablesToQuotes(persister.all(Quote.Kind))
	def bySource(source:String):List[Quote] = savablesToQuotes(persister.search(Quote.Kind,Quote.Source,source))
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
		indexWriter.addDocument(quote)
		indexWriter.commit
		key
	}
}

