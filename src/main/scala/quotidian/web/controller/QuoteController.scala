package quotidian.web.controller

import com.bryanjswift.persistence.Savable
import quotidian.Logging
import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister

object QuoteController extends Logging {
	def getAll():List[Quote] = {
		val savables:List[Savable] = DatastorePersister.getAll(Quote.kind)
		for {
			savable <- savables
		} yield savable.asInstanceOf[Quote]
	}
}
