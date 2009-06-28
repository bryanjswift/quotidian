package quotidian.web.controller

import com.bryanjswift.persistence.Savable
import quotidian.model.Quote
import quotidian.persistence.datastore.DatastorePersister

object QuoteController {
	def getAll():List[Quote] = {
		val savables:List[Savable] = DatastorePersister.getAll("Quote")
		for {
			savable <- savables
		} yield savable.asInstanceOf[Quote]
	}
}
