package quotidian.web.controller

import basic.persistence.{Persister,Savable}
import java.io.Serializable
import quotidian.Logging
import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister

abstract class QuoteController extends Logging {
// TODO: DatastorePersister should be abstracted to a Persister
	def persister:Persister
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
	def save(quote:Quote):Serializable = DatastorePersister.save(quote)
}
