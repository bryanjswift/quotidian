package quotidian.search

import com.google.appengine.api.datastore.{DatastoreServiceFactory,Entity,EntityNotFoundException}
import java.util.ConcurrentModificationException
import org.apache.lucene.store.{Lock,LockFactory}
import quotidian.Logging

class DatastoreLockFactory extends LockFactory with Logging {
	private var locks = Map[String,Lock]()
	def clearLock(lockName:String):Unit = {
		val lock = if (locks.contains(lockName)) { locks(lockName) } else { null }
		locks = if (lock != null) {
			lock.release
			locks - lockName
		} else {
			locks
		}
	}
	def makeLock(lockName:String):Lock = {
		val lock = DatastoreLock(lockName)
		locks = locks + (lockName -> lock)
		lock
	}
}

class DatastoreLock(private val lockName:String) extends Lock with Logging {
	private val lock = new Entity(DatastoreLock.Kind,lockName)
	def isLocked:Boolean = {
		try {
			DatastoreLock.datastore.get(lock.getKey)
			true
		} catch {
			case enfe:EntityNotFoundException => { false }
			case e:Exception => {
				warn("Failed to check for lock on " + lockName)
				false
			}
		}
	}
	def obtain:Boolean = {
		val txn = DatastoreLock.datastore.beginTransaction
		try {
			DatastoreLock.datastore.get(txn, lock.getKey)
			txn.commit
			false
		} catch {
			case enfe:EntityNotFoundException => {
				try {
					DatastoreLock.datastore.put(txn, lock)
					txn.commit
					true
				} catch {
					case cme:ConcurrentModificationException => {
						// something created entity between get and put
						txn.commit
						false
					}
				}
			}
		}
	}
	def release:Unit = {
		val txn = DatastoreLock.datastore.beginTransaction
		try {
			DatastoreLock.datastore.delete(txn, lock.getKey)
			txn.commit
		} catch {
			case e:Exception => {
				warn("unable to release lock for " + lockName)
			}
		}
	}
}

object DatastoreLock {
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def apply(lockName:String) = new DatastoreLock(lockName)
	val Kind = "lock"
}
