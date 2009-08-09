package quotidian.web.controller

import quotidian.persistence.datastore.DatastorePersister
import quotidian.search.DatastoreDirectory

class DatastoreQuoteController extends QuoteController {
	val persister = DatastorePersister
	val directory = new DatastoreDirectory()
}
