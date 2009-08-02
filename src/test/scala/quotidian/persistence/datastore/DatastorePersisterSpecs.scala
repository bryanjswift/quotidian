package quotidian.persistence.datastore

import java.io.Serializable
import quotidian.DatastoreSpecification
import quotidian.model.Quote

object DatastorePersisterSpecs extends DatastoreSpecification {
	"saving a Savable" should {
		"return a Serializable id" >> {
			val quote = new Quote("This is a test","source","context")
			val id = DatastorePersister.save(quote)
			id must haveSuperClass[Serializable]
		}
	}
}
