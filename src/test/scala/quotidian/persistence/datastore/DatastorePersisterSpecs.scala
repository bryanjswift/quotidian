package quotidian.persistence.datastore

import java.io.Serializable
import quotidian.DatastoreSpecification
import quotidian.model.Quote

object DatastorePersisterSpecs extends DatastoreSpecification {
	val q1 = Quote("This is a test","source","context")
	val q2 = Quote("Another test","source2","context2")
	"saving a Savable" should {
		datastoreCleanup.before
		"return a Serializable id" >> {
			val id = DatastorePersister.save(q1)
			id must haveSuperClass[Serializable]
		}
		"allow retrieval of Savable from returned id" >> {
			val id = DatastorePersister.save(q1)
			val retrieved = DatastorePersister.get(Quote.kind,id)
			q1 mustEqual retrieved
		}
	}
	"saving multiple Savables" should {
		datastoreCleanup.before
		"return a different id for each Savable" >> {
			val id1 = DatastorePersister.save(q1)
			val id2 = DatastorePersister.save(q2)
			id1 must be_!=(id2)
		}
	}
	"counting all Savables" should {
		datastoreCleanup.before
		"return 0 with none saved" >> {
			0 mustEqual DatastorePersister.count(Quote.kind)
		}
		"return 1 with one saved" >> {
			val id1 = DatastorePersister.save(q1)
			1 mustEqual DatastorePersister.count(Quote.kind)
		}
		"return 2 with two saved" >> {
			val id1 = DatastorePersister.save(q1)
			val id2 = DatastorePersister.save(q2)
			2 mustEqual DatastorePersister.count(Quote.kind)
		}
	}
	"retrieving all Savables" should {
		datastoreCleanup.before
		"return a list containing each of the saved Savables" >> {
			val id1 = DatastorePersister.save(q1)
			val id2 = DatastorePersister.save(q2)
			val savables = DatastorePersister.all(Quote.kind)
			2 mustEqual savables.size
			savables must containAll(List(q1,q2))
		}
	}
}
