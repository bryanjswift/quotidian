package quotidian.search

import org.apache.lucene.store.IndexInput

class DatastoreIndexInput(private var file:DatastoreFile) extends IndexInput {
	def this() = this(DatastoreFile())
	def close:Unit = { /* nothing to do here */ }
	def getFilePointer():Long = file.position
	def length:Long = file.length
	def readByte:Byte = file.read
	def readBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = { /* TODO: IMPLEMENT ME */ }
	def seek(pos:Long):Unit = file = file.seek(pos)
}

