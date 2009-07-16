package quotidian.web.controller

import com.bryanjswift.persistence.Savable
import quotidian.Logging
import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister

object QuoteController extends Logging {
	private var quotes = List[Quote]()
	def all:List[Quote] = {
		savablesToQuotes(DatastorePersister.getAll(Quote.kind))
	}
	def random:Quote = {
		val quotes = all
		val position = (quotes.length * Math.random).toInt
		quotes(position)
	}
	def bySource(source:String):List[Quote] = {
		savablesToQuotes(DatastorePersister.search(Quote.kind,"source",source))
	}
	private def savablesToQuotes(savables:List[Savable]) = {
		for {
			savable <- savables
		} yield savable.asInstanceOf[Quote]
	}
}
