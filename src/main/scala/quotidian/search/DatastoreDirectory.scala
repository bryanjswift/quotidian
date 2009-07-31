package quotidian.search

import DatastoreDirectory.{datastore,save,fileByName}
import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,PreparedQuery,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withChunkSize}
import java.io.Serializable
import java.util.Calendar
import org.apache.lucene.store.{Directory,IndexInput,IndexOutput}

class DatastoreDirectory extends Directory {
	def close:Unit = { }
	def createOutput(name:String):IndexOutput = null
	def deleteFile(name:String):Unit = {
		val file = fileByName(name)
		datastore.delete(file.entity.getKey)
	}
	def fileExists(name:String):Boolean = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		query.countEntities > 0
	}
	def fileLength(name:String):Long = fileByName(name).length
	def fileModified(name:String):Long = fileByName(name).dateModified
	def list:Array[String] = {
		val query = datastore.prepare(new Query(DatastoreFile.Kind))
		val entities = query.asIterator(withChunkSize(Integer.MAX_VALUE))
		var names = List[String]()
		while (entities.hasNext) {
			names = entities.next.getProperty(DatastoreFile.Filename).toString :: names
		}
		names.toArray
	}
	def openInput(name:String):IndexInput = null
	def renameFile(from:String,to:String):Unit = {
		val file = fileByName(from)
		save(DatastoreFile.rename(file,to))
	}
	def touchFile(name:String):Unit = save(fileByName(name))
}

object DatastoreDirectory {
	val datastore = DatastoreServiceFactory.getDatastoreService()
	def save(file:DatastoreFile):Serializable = {
		val date = Calendar.getInstance.getTimeInMillis
		datastore.put(file.set(DatastoreFile.DateModified,date).entity)
	}
	def fileByName(name:String):DatastoreFile = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		try {
			DatastoreFile(query.asSingleEntity)
		} catch {
			case tmre:PreparedQuery.TooManyResultsException => null
		}
	}
}

