package quotidian.search

import DatastoreFile.{Contents,DateModified,Deleted,Filename,Kind,Size}
import com.google.appengine.api.datastore.Entity
import java.io.IOException
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
	def dateModified:Long = ent.getProperty(DatastoreFile.DateModified).asInstanceOf[Long]
	/** Number of bytes in the file
		* @return size of the file*/
	def length:Int = bytes.length
	/** Reads the byte at the current position and advances the position by one
		* @return tuple containing byte found at the current position and file with position advanced by one */
	def read:ByteAndFile = new ByteAndFile(bytes(position),seek(position + 1))
	/** Read byte at offset into bits at current if current is less than or equal to length
		* @param bits				array of bytes to write into
		* @param offset			position in file's bytes to read from
		* @param len				total number of bytes to read
		* @returns array of bytes written into while reading */
	def read(bits:Array[Byte],offset:Int,len:Int):Array[Byte] = {
		if (bits.length < len) throw new IOException("Array to read into is not large enough for the length specified")
		read(0,bits,offset,len)
		bits
	}
	/** Read byte at offset into bits at current if current is less than or equal to length
		* @param current		the current position to write to in bits
		* @param bits				array of bytes to write into
		* @param offset			position in file's bytes to read from
		* @param len				total number of bytes to read
		* @returns array of bytes written into while reading */
	private def read(current:Int,bits:Array[Byte],offset:Int,len:Int):Array[Byte] = {
		if (current == len) {
			bits
		} else {
			bits(current) = if (offset < len) bytes(offset) else 0
			read(current + 1,bits,offset + 1,len)
		}
	}
	/** Move the position pointer to the new position
		* @param pos				new pointer position to set
		* @return file with the new pointer set */
	def seek(pos:Int):DatastoreFile = DatastoreFile(pos,bytes,entity)
	/** Move the position pointer to the new position
		* @param pos				new pointer position to set
		* @return file with the new pointer set
		* @throws IllegalArgumentException when pos is greater than scala.Math.MAX_INT */
	def seek(pos:Long):DatastoreFile = {
		if (pos > scala.Math.MAX_INT) throw new IOException("pos is too large to be used as an array index")
		else seek(pos.asInstanceOf[Int])
	}
	/** Sets a property on the underlying Entity
		* @param property		name of the property to set
		* @param value			value to set into property
		* @return file with the new property set */
	def set(property:String,value:Any):DatastoreFile = {
		ent.setProperty(property,value)
		DatastoreFile(position,bytes,ent)
	}
	/** Write a new byte to the current position filling in any space between the current length and the position
		* Advances position by one
		* @param b					the byte to write
		* @return file with the new byte writte */
	def write(b:Byte):DatastoreFile = {
		if (position == length) {
			DatastoreFile(position + 1,bytes ++ Array(b),entity)
		} else if (position > length) {
			DatastoreFile(position + 1,bytes ++ new Array[Byte](position - length) ++ Array(b),entity)
		} else {
			DatastoreFile(position + 1,(bytes.take(position) ++ Array(b) ++ bytes.drop((position + 1))).toArray,entity)
		}
	}
	/** Write the provided array of bytes into this file's contents
		* @param bits				array of bytes to be written to file
		* @param offset			position in file to start writing bytes
		* @param len				number of bytes to write
		* @return file with bits written to it */
	def write(bits:Array[Byte],offset:Int,len:Int):DatastoreFile = write(0,bits,offset,len)
	/** Write the provided array of bytes into this file's contents
		* @param current		current position in the bits array
		* @param bits				array of bytes to be written to file
		* @param offset			position in file to start writing bytes
		* @param len				number of bytes to write
		* @return file with bits written to it */
	private def write(current:Int,bits:Array[Byte],offset:Int,len:Int):DatastoreFile = {
		// This could be written using bytes.take(offset) ++ bits ++ bytes.drop(offset + len) or something similar
		if (current == len) {
			DatastoreFile(position + current,bytes,ent)
		} else {
			bytes(offset) = if (current < bits.length) bits(current) else 0
			write(current + 1,bits,offset + 1,len)
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
	def apply(filename:String):DatastoreFile = {
		val entity = new Entity(Kind)
		entity.setProperty(Filename,filename)
		apply(entity)
	}
	def rename(file:DatastoreFile,to:String) = file.set(Filename,to)
}

sealed case class ByteAndFile(val byte:Byte,val file:DatastoreFile)
