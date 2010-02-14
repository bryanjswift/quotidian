package quotidian.web.controller

import quotidian.persistence.datastore.DatastorePersister
import quotidian.search.DatastoreDirectory

trait DatastoreController {
	protected val persister = DatastoreController.persister
	protected val directory = DatastoreController.directory
	protected val MaxPerPage = 10
}

private object DatastoreController {
	private val persister = DatastorePersister
	private val directory = new DatastoreDirectory
}

class DatastoreQuoteController extends QuoteController with DatastoreController 
class DatastoreSearchController extends SearchController with DatastoreController
