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
		val entity = fileByName(name)
		datastore.delete(entity.getKey)
	}
	def fileExists(name:String):Boolean = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		query.countEntities > 0
	}
	def fileLength(name:String):Long = fileByName(name).getProperty(DatastoreFile.Size).asInstanceOf[Long]
	def fileModified(name:String):Long = fileByName(name).getProperty(DatastoreFile.DateModified).asInstanceOf[Long]
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
		val entity = fileByName(from)
		entity.setProperty(DatastoreFile.Filename,to)
		save(entity)
	}
	def touchFile(name:String):Unit = save(fileByName(name))
}

object DatastoreDirectory {
	val datastore = DatastoreServiceFactory.getDatastoreService()
	def save(entity:Entity):Serializable = {
		val date = Calendar.getInstance.getTimeInMillis
		entity.setProperty(DatastoreFile.DateModified,date)
		datastore.put(entity)
	}
	def fileByName(name:String):Entity = {
		val query = datastore.prepare(
			new Query(DatastoreFile.Kind).addFilter(DatastoreFile.Filename,Query.FilterOperator.EQUAL,name))
		try {
			query.asSingleEntity
		} catch {
			case tmre:PreparedQuery.TooManyResultsException => null
		}
	}
}

class DatastoreIndexInput(private val entity:Entity) extends IndexInput {
	def this() = this(new Entity(DatastoreFile.Kind))
	def close:Unit = { }
	def getFilePointer():Long = 0
	def length:Long = 0
	def readByte:Byte = 0
	def readBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = { }
	def seek(pos:Long):Unit = { }
}

class DatastoreIndexOutput(private val entity:Entity) extends IndexOutput {
	def this() = this(new Entity(DatastoreFile.Kind))
	def close:Unit = { }
	def flush:Unit = DatastoreDirectory.save(entity)
	def getFilePointer():Long = 0
	def length:Long = 0
	def seek(pos:Long):Unit = { }
	def writeByte(b:Byte):Unit = { }
	def writeBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = { }
}
