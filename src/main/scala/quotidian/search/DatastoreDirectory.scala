package quotidian.search

import DatastoreDirectory.fileByName
import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,PreparedQuery,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withChunkSize}
import java.io.{IOException,Serializable}
import java.util.Calendar
import org.apache.lucene.store.{Directory,IndexInput,IndexOutput,LockFactory,NoLockFactory}

class DatastoreDirectory extends Directory with Logging {
	lockFactory = new DatastoreLockFactory
	def close:Unit = { /* nothing to do here */ }
	def createOutput(name:String):IndexOutput = new DatastoreIndexOutput(this,fileByName(name))
	def deleteFile(name:String):Unit = DatastoreDirectory.delete(name)
	def fileExists(name:String):Boolean = DatastoreDirectory.fileExists(name)
	def fileLength(name:String):Long = fileByName(name).length
	def fileModified(name:String):Long = fileByName(name).dateModified
	def list:Array[String] = DatastoreDirectory.listFiles
	def openInput(name:String):IndexInput = new DatastoreIndexInput(this,fileByName(name))
	def renameFile(from:String,to:String):Unit = {
		if (!DatastoreDirectory.fileExists(from)) throw new IOException("No file called {from} exists")
		val file = fileByName(from)
		DatastoreDirectory.save(DatastoreFile.rename(file,to))
	}
	def save(file:DatastoreFile) = DatastoreDirectory.save(file)
	override def setLockFactory(lf:LockFactory):Unit = { /* nothing to do here */ }
	def touchFile(name:String):Unit = DatastoreDirectory.save(fileByName(name))
	override def toString = DatastoreDirectory.listFiles.mkString("FilesNames[",",","]")
}

object DatastoreDirectory {
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	private def delete(name:String):Unit = {
		if (!fileExists(name)) throw new IOException("No file called {name} exists")
		val file = fileByName(name)
		datastore.delete(file.entity.getKey)
	}
	private def fileByName(name:String):DatastoreFile = {
		if (fileExists(name)) {
			val query = datastore.prepare(
				new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
			DatastoreFile(query.asSingleEntity)
		} else { DatastoreFile(name) }
	}
	private def fileExists(name:String):Boolean = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		query.countEntities > 0
	}
	private def listFiles:Array[String] = {
		val q = new Query(DatastoreFile.Kind).addSort(DatastoreFile.DateModified,Query.SortDirection.DESCENDING)
		val query = datastore.prepare(q)
		val entities = query.asIterator(withChunkSize(Integer.MAX_VALUE))
		var names = List[String]()
		while (entities.hasNext) {
			names = entities.next.getProperty(DatastoreFile.Filename).toString :: names
		}
		names.toArray
	}
	private def save(file:DatastoreFile):Serializable = {
		datastore.put(file.set(DatastoreFile.DateModified,Calendar.getInstance.getTimeInMillis).entity)
	}
}

