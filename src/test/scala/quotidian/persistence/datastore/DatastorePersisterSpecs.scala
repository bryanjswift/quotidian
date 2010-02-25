package quotidian.persistence.datastore

import java.io.Serializable
import quotidian.DatastoreSpecification
import quotidian.model.Quote

class DatastorePersisterSpecs extends DatastoreSpecification {
	val q1 = Quote("This is a test","source","context")
	val q2 = Quote("Another test","source2","context2")
	"saving a Savable" should {
		datastoreCleanup.after
		"return a Serializable id" >> {
			val id = DatastorePersister.save(q1)
			id must haveSuperClass[Serializable]
		}
		"allow retrieval of Savable from returned id" >> {
			val id = DatastorePersister.save(q1)
			val retrieved = DatastorePersister.get(Quote.Kind,id)
			q1 mustEqual retrieved
		}
	}
	"saving multiple Savables" should {
		datastoreCleanup.after
		"return a different id for each Savable" >> {
			val id1 = DatastorePersister.save(q1)
			val id2 = DatastorePersister.save(q2)
			id1 must be_!=(id2)
		}
	}
	"counting all Savables" should {
		datastoreCleanup.after
		"return 0 with none saved" >> {
			DatastorePersister.count(Quote.Kind) mustEqual 0
		}
		"return 1 with one saved" >> {
			val id1 = DatastorePersister.save(q1)
			DatastorePersister.count(Quote.Kind) mustEqual 1
		}
		"return 2 with two saved" >> {
			val id1 = DatastorePersister.save(q1)
			val id2 = DatastorePersister.save(q2)
			DatastorePersister.count(Quote.Kind) mustEqual 2
		}
	}
	"retrieving all Savables" should {
		datastoreCleanup.after
		"return a list containing each of the saved Savables" >> {
			val id1 = DatastorePersister.save(q1)
			val id2 = DatastorePersister.save(q2)
			val savables = DatastorePersister.all(Quote.Kind)
			savables.size mustEqual 2
			savables must containAll(List(q1,q2))
		}
	}
}
