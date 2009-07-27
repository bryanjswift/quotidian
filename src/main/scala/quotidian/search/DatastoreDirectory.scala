package quotidian.search

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withChunkSize}
import org.apache.lucene.store.{Directory,IndexInput,IndexOutput}

class DatastoreDirectory extends Directory {
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def close:Unit = { }
	def createOutput(name:String):IndexOutput = null
	def deleteFile(name:String):Unit = { }
	def fileExists(name:String):Boolean = {
		val query = datastore.prepare(
			new Query(DatastoreDirectory.kind).addFilter(DatastoreDirectory.filename,Query.FilterOperator.EQUAL,name))
		query.countEntities > 0
	}
	def fileLength(name:String):Long = 0
	def fileModified(name:String):Long = 0
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
	def renameFile(from:String,to:String):Unit = { }
	def touchFile(name:String):Unit = { }
}

object DatastoreDirectory {
	val kind = "DatastoreFile"
	val filename = "filename"
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
