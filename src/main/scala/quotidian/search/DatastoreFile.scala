package quotidian.search

import DatastoreFile.{Contents,DateModified,Deleted,Filename,Kind,Size}
import com.google.appengine.api.datastore.Entity
import java.util.Calendar

/** Representation of a file which relies on Datastore Entity objects
	*
	* Primary constructor providing position, bytes and entity
	* @param position		starting position for reading/writing from bytes
	* @param bytes			array of bytes representing the starting data of this file
	* @param ent				savable datastore representation of the file */
class DatastoreFile(val position:Int, private val bytes:Array[Byte], private val ent:Entity) {
	/** Default constructor, no entity is provided so a new one is created */
	private def this() = this(0,new Array[Byte](1),new Entity(Kind))
	/** Constructor with entity and initial position
		* @param position		starting position for reading/writing from bytes
		* @param entity			savable datastore representation of the file */
	private def this(position:Int, entity:Entity) = this(position,entity.getProperty(Contents).asInstanceOf[Array[Byte]],entity)
	/** Constructor with only an entity, bytes are read from entity and position is set to 0
		* @param entity			savable datastore representation of the file */
	private def this(entity:Entity) = this(0,entity)
	/** Last modified date of the file
		* @return time in milliseconds when this file was last modified */
	def dateModified = ent.getProperty(DatastoreFile.DateModified).asInstanceOf[Long]
	/** Number of bytes in the file
		* @return size of the file*/
	def length:Int = bytes.length
	/** Reads the byte at the current position
		* @return byte found at the current position*/
	def read:Byte = bytes(position)
	/** Move the position pointer to the new position
		* @param pos				new pointer position to set
		* @return file with the new pointer set */
	def seek(pos:Int) = DatastoreFile(pos,bytes,entity)
	/** Sets a property on the underlying Entity
		* @param property		name of the property to set
		* @param value			value to set into property
		* @return file with the new property set */
	def set(property:String,value:Any) = {
		ent.setProperty(property,value)
		DatastoreFile(position,bytes,ent)
	}
	/** Write a new byte to the current position filling in any space between the current length and the position
		* @param b					the byte to write
		* @return file with the new byte writte */
	def write(b:Byte) = {
		if (position == length) {
			DatastoreFile(position,bytes ++ Array(b),entity)
		} else if (position > length) {
			DatastoreFile(position,bytes ++ new Array[Byte](position - length) ++ Array(b),entity)
		} else {
			DatastoreFile(position,(bytes.take(position) ++ Array(b) ++ bytes.drop((position + 1))).toArray,entity)
		}
	}
	/** Retrieve the underlying entity
		* @return the datastore representation which can be sent to storage */
	def entity:Entity = set(Contents,bytes).set(Size,length).set(Deleted,false).ent
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
	def rename(file:DatastoreFile,to:String) = file.set(Filename,to)
}
