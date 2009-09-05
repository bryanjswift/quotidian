package quotidian.web.controller

import quotidian.persistence.datastore.DatastorePersister
import quotidian.search.DatastoreDirectory

class DatastoreQuoteController extends QuoteController {
	protected val persister = DatastoreQuoteController.persister
	protected val directory = DatastoreQuoteController.directory
}

private object DatastoreQuoteController {
	private val persister = DatastorePersister
	private val directory = new DatastoreDirectory
}
