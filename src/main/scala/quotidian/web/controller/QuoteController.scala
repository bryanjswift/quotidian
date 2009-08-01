package quotidian.web.controller

import com.bryanjswift.persistence.{Persister,Savable}
import java.io.Serializable
import quotidian.Logging
import quotidian.model.Quote

abstract class QuoteController extends Logging {
	def persister:Persister
	def all:List[Quote] = savablesToQuotes(persister.getAll(Quote.kind))
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
	def save(quote:Quote):Serializable = persister.save(quote)
}
