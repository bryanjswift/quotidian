package quotidian.search

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,PreparedQuery,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withChunkSize}
import java.io.Serializable
import java.util.Calendar
import org.apache.lucene.store.{Directory,IndexInput,IndexOutput}

class DatastoreDirectory extends Directory {
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def close:Unit = { }
	def createOutput(name:String):IndexOutput = null
	def deleteFile(name:String):Unit = {
		val entity = fileByName(name)
		datastore.delete(entity.getKey)
	}
	def fileExists(name:String):Boolean = {
		val query = datastore.prepare(
			new Query(DatastoreDirectory.kind).addFilter(DatastoreDirectory.filename,Query.FilterOperator.EQUAL,name))
		query.countEntities > 0
	}
	def fileLength(name:String):Long = fileByName(name).getProperty(DatastoreDirectory.size).asInstanceOf[Long]
	def fileModified(name:String):Long = fileByName(name).getProperty(DatastoreDirectory.dateModified).asInstanceOf[Long]
	private def fileByName(name:String):Entity = {
		val query = datastore.prepare(
			new Query(DatastoreDirectory.kind).addFilter(DatastoreDirectory.filename,Query.FilterOperator.EQUAL,name))
		try {
			query.asSingleEntity
		} catch {
			case tmre:PreparedQuery.TooManyResultsException => null
		}
	}
	def list:Array[String] = {
		val query = datastore.prepare(new Query(DatastoreDirectory.kind))
		val entities = query.asIterator(withChunkSize(Integer.MAX_VALUE))
		var names = List[String]()
		while (entities.hasNext) {
			names = entities.next.getProperty(DatastoreDirectory.filename).toString :: names
		}
		names.toArray
	}
	def openInput(name:String):IndexInput = null
	def renameFile(from:String,to:String):Unit = {
		val entity = fileByName(from)
		entity.setProperty(DatastoreDirectory.filename,to)
		save(entity)
	}
	private def save(entity:Entity):Serializable = {
		val date = Calendar.getInstance.getTimeInMillis
		entity.setProperty(DatastoreDirectory.dateModified,date)
		datastore.put(entity)
	}
	def touchFile(name:String):Unit = save(fileByName(name))
}

object DatastoreDirectory {
	val contents = "contents"
	val dateModified = "datemodified"
	val deleted = "deleted"
	val filename = "filename"
	val kind = "DatastoreFile"
	val size = "size"
}

class DatastoreIndexInput extends IndexInput {
	def close:Unit = { }
	def getFilePointer():Long = 0
	def length:Long = 0
	def readByte:Byte = 0
	def readBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = { }
	def seek(pos:Long):Unit = { }
}

class DatastoreIndexOutput extends IndexOutput {
	def close:Unit = { }
	def flush:Unit = { }
	def getFilePointer():Long = 0
	def length:Long = 0
	def seek(pos:Long):Unit = { }
	def writeByte(b:Byte):Unit = { }
	def writeBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = { }
}
