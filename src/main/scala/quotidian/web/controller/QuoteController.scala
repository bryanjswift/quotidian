package quotidian.web.controller

import com.bryanjswift.persistence.Savable
import java.io.Serializable
import quotidian.Logging
import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister

object QuoteController extends Logging {
// TODO: DatastorePersister should be abstracted to a Persister
	def all:List[Quote] = savablesToQuotes(DatastorePersister.getAll(Quote.kind))
	def bySource(source:String):List[Quote] = savablesToQuotes(DatastorePersister.search(Quote.kind,"source",source))
	def get(id:Serializable):Quote = DatastorePersister.get(Quote.kind,id).asInstanceOf[Quote]
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
	def save(quote:Quote):Serializable = DatastorePersister.save(quote)
}
