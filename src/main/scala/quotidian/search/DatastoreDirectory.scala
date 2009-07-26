package quotidian.search

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,Key,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withLimit}
import org.apache.lucene.store.{Directory,IndexInput,IndexOutput}

class DatastoreDirectory extends Directory {
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def close:Unit = { }
	def createOutput(name:String):IndexOutput = null
	def deleteFile(name:String):Unit = { }
	def fileExists(name:String):Boolean = false
	def fileLength(name:String):Long = 0
	def fileModified(name:String):Long = 0
	def list:Array[String] = null
	def openInput(name:String):IndexInput = null
	def renameFile(from:String,to:String):Unit = { }
	def touchFile(name:String):Unit = { }
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
