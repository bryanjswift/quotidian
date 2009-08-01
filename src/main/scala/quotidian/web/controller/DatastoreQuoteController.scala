package quotidian.web.controller

import quotidian.persistence.datastore.DatastorePersister

class DatastoreQuoteController extends QuoteController {
	val persister = DatastorePersister
}
