package quotidian.search

import org.apache.lucene.store.IndexOutput
import java.util.zip.{Checksum,CRC32}

class DatastoreIndexOutput(private val directory:DatastoreDirectory, private var file:DatastoreFile) extends IndexOutput {
	private val digest:Checksum = new CRC32
	def this(directory:DatastoreDirectory) = this(directory,DatastoreFile())
	def close:Unit = directory.save(file)
	def flush:Unit = { /* nothing to do here */ }
	def getFilePointer():Long = file.position
	def length:Long = file.length
	def seek(pos:Long):Unit = file = file.seek(pos)
	def writeByte(b:Byte):Unit = {
		digest.update(b)
		file = file.write(b)
	}
	def writeBytes(bytes:Array[Byte],offset:Int,length:Int):Unit = file = file.write(bytes,offset,length)
}
