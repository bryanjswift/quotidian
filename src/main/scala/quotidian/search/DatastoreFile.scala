package quotidian.search

import _root_.quotidian.search.DatastoreFile._
import com.google.appengine.api.datastore.Entity

class DatastoreFile(private val position:Int, private val bytes:Array[Byte], private val entity:Entity) {
	private def this() = this(0,new Array[Byte](1),new Entity(Kind))
	private def this(position:Int, entity:Entity) = this(position,entity.getProperty(Contents).asInstanceOf[Array[Byte]],entity)
	private def this(entity:Entity) = this(0,entity)
	def seek(pos:Int) = DatastoreFile(pos,bytes,entity)
	def length:Int = bytes.length
	def read:Byte = bytes(position)
	def write(b:Byte) = {
		if (position == length) {
			DatastoreFile(position,bytes ++ Array(b),entity)
		} else if (position > length) {
			DatastoreFile(position,bytes ++ new Array[Byte](position - length) ++ Array(b),entity)
		} else {
			DatastoreFile(position,(bytes.take(position) ++ Array(b) ++ bytes.drop((position + 1))).toArray,entity)
		}
	}
}

object DatastoreFile {
	val Contents = "contents"
	val DateModified = "datemodified"
	val Deleted = "deleted"
	val Filename = "filename"
	val Kind = "DatastoreFile"
	val Size = "size"
	def apply() = new DatastoreFile()
	def apply(entity:Entity) = new DatastoreFile(entity)
	def apply(position:Int,entity:Entity) = new DatastoreFile(position,entity)
	def apply(position:Int,bytes:Array[Byte],entity:Entity) = new DatastoreFile(position,bytes,entity)
}
