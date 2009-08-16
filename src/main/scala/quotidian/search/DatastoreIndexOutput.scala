package quotidian.search

import org.apache.lucene.store.IndexOutput
import java.util.zip.{Checksum,CRC32}

class DatastoreIndexOutput(private val directory:DatastoreDirectory, private var file:DatastoreFile) extends IndexOutput {
	def this(directory:DatastoreDirectory) = this(directory,DatastoreFile())
	def close:Unit = flush
	def flush:Unit = directory.save(file)
	def getFilePointer():Long = file.position
	def length:Long = file.length
	def seek(pos:Long):Unit = file = file.seek(pos)
	def writeByte(b:Byte):Unit = file = file.write(b)
	def writeBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = file = file.write(bytes,offset,length)
}
