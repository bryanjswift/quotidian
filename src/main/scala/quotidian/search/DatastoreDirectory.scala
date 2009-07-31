package quotidian.search

import DatastoreDirectory.fileByName
import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,PreparedQuery,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withChunkSize}
import java.io.{IOException,Serializable}
import java.util.Calendar
import org.apache.lucene.store.{Directory,IndexInput,IndexOutput}

class DatastoreDirectory extends Directory {
	def close:Unit = { /* nothing to do here */ }
	def createOutput(name:String):IndexOutput = new DatastoreIndexOutput(fileByName(name))
	def deleteFile(name:String):Unit = DatastoreDirectory.delete(name)
	def fileExists(name:String):Boolean = DatastoreDirectory.fileExists(name)
	def fileLength(name:String):Long = fileByName(name).length
	def fileModified(name:String):Long = fileByName(name).dateModified
	def list:Array[String] = DatastoreDirectory.listFiles
	def openInput(name:String):IndexInput = new DatastoreIndexInput(fileByName(name))
	def renameFile(from:String,to:String):Unit = {
		if (!DatastoreDirectory.fileExists(from)) throw new IOException("No file called {from} exists")
		val file = fileByName(from)
		DatastoreDirectory.save(DatastoreFile.rename(file,to))
	}
	def touchFile(name:String):Unit = DatastoreDirectory.save(fileByName(name))
}

object DatastoreDirectory {
	val datastore = DatastoreServiceFactory.getDatastoreService()
	def delete(name:String):Unit = {
		if (!fileExists(name)) throw new IOException("No file called {name} exists")
		val file = fileByName(name)
		datastore.delete(file.entity.getKey)
	}
	def fileByName(name:String):DatastoreFile = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		if (fileExists(name)) DatastoreFile(query.asSingleEntity)
		else DatastoreFile(name)
	}
	def fileExists(name:String):Boolean = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		query.countEntities > 0
	}
	def listFiles:Array[String] = {
		val query = datastore.prepare(new Query(DatastoreFile.Kind))
		val entities = query.asIterator(withChunkSize(Integer.MAX_VALUE))
		var names = List[String]()
		while (entities.hasNext) {
			names = entities.next.getProperty(DatastoreFile.Filename).toString :: names
		}
		names.toArray
	}
	def save(file:DatastoreFile):Serializable = {
		val date = Calendar.getInstance.getTimeInMillis
		datastore.put(file.set(DatastoreFile.DateModified,date).entity)
	}
}

