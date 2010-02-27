package quotidian.web.controller

import basic.persistence.{Persister,Savable}
import java.io.Serializable
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{IndexWriter,SerialMergeScheduler}
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version
import quotidian.{Config,Logging}
import quotidian.model.Quote

abstract class QuoteController extends Logging {
	protected def persister:Persister
	protected def directory:Directory
	protected def MaxPerPage:Int
	protected def writer:IndexWriter = new IndexWriter(directory,new StandardAnalyzer(Version.LUCENE_30),UNLIMITED)
	def enqueue(id:Serializable):Unit
	def all:List[Quote] = persister.all(Quote.Kind)
	def delete(id:Serializable):Unit = persister.delete(Quote.Kind,id)
	def get(id:Serializable):Quote = persister.get(Quote.Kind,id).asInstanceOf[Quote]
	def random:Quote = {
		val quotes = all
		val position = (quotes.length * Math.random).toInt
		quotes(position)
	}
	def save(quote:Quote) = persister.save(quote)
	def index(quote:Quote) {
		val w = writer
		w.setMergeScheduler(new SerialMergeScheduler)
		try {
			w.addDocument(quote)
			w.commit
		} finally { w.close }
	}
	def page(pageNumber:Int):List[Quote] = persister.some(Quote.Kind,MaxPerPage,(pageNumber - 1) * MaxPerPage)

	private[controller] def saveAndIndex(quote:Quote) = {
		val key = save(quote)
		index(quote)
		key
	}

	implicit private def savables2quotes(savables:List[Savable]):List[Quote] = {
		for {
			savable <- savables
		} yield savable.asInstanceOf[Quote]
	}
	implicit private def savable2quote(savable:Savable):Quote = savable.asInstanceOf[Quote]
}
