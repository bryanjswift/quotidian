package quotidian.web.controller

import quotidian.persistence.datastore.DatastorePersister

object DatastoreQuoteController extends QuoteController {
	val persister = DatastorePersister
}
