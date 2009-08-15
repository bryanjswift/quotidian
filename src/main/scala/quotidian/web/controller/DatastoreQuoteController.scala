package quotidian.web.controller

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriter.MaxFieldLength.UNLIMITED
import quotidian.persistence.datastore.DatastorePersister
import quotidian.search.DatastoreDirectory

class DatastoreQuoteController extends QuoteController {
	val persister = DatastoreQuoteController.persister
	val directory = DatastoreQuoteController.directory
	lazy val writer = DatastoreQuoteController.writer
}

private object DatastoreQuoteController {
	private val persister = DatastorePersister
	private val directory = new DatastoreDirectory()
	private lazy val writer = new IndexWriter(directory,new StandardAnalyzer(),UNLIMITED)
}
