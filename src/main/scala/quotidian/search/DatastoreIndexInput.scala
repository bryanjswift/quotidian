package quotidian.search

import org.apache.lucene.store.IndexInput

class DatastoreIndexInput(private val directory:DatastoreDirectory, private var file:DatastoreFile) extends IndexInput {
	def this(directory:DatastoreDirectory) = this(directory,DatastoreFile())
	def close:Unit = { /* nothing to do here */ }
	def getFilePointer():Long = file.position
	def length:Long = file.length
	def readByte:Byte = {
		val byteAndFile = file.read
		file = byteAndFile.file
		byteAndFile.byte
	}
	def readBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = file.read(bytes,offset,length)
	def seek(pos:Long):Unit = file = file.seek(pos)
}

