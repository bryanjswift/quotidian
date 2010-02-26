package quotidian.search

import DatastoreFile.{Contents,DateModified,Deleted,Filename,Kind,Size}
import com.google.appengine.api.datastore.{Blob,Entity}
import java.io.IOException
import java.util.Calendar
import quotidian.Logging

/** Representation of a file which relies on Datastore Entity objects
	*
	* Primary constructor providing position, bytes and entity
	* @param position		starting position for reading/writing from bytes
	* @param bytes			list of bytes representing the starting data of this file
	* @param ent				savable datastore representation of the file */
class DatastoreFile(val position:Int, private val bytes:List[Byte], private val ent:Entity) extends Logging {
	/** Default constructor, no entity is provided so a new one is created */
	private def this() = this(0,List[Byte](),new Entity(Kind,DatastoreDirectory.index))
	/** Constructor with entity, initial position and an array
		* @param position		starting position for reading/writing from bytes
		* @param bytes			array of bytes representing the starting data of this file
		* @param entity			savable datastore representation of the file */
	private def this(position:Int, bytes:Array[Byte], entity:Entity) = this(position,bytes.toList,entity)
	/** Constructor with entity and initial position
		* @param position		starting position for reading/writing from bytes
		* @param entity			savable datastore representation of the file */
	private def this(position:Int, entity:Entity) = this(position,entity.getProperty(Contents).asInstanceOf[Blob].getBytes,entity)
	/** Constructor with only an entity, bytes are read from entity and position is set to 0
		* @param entity			savable datastore representation of the file */
	private def this(entity:Entity) = this(0,entity)
	/** Last modified date of the file
		* @return time in milliseconds when this file was last modified */
	def dateModified:Long = ent.getProperty(DatastoreFile.DateModified).asInstanceOf[Long]
	/** Number of bytes in the file */
	lazy val length:Int = bytes.length
	/** Reads the byte at the current position and advances the position by one
		* @return tuple containing byte found at the current position and file with position advanced by one */
	def read:ByteAndFile = {
		if (position < length) {
			new ByteAndFile(bytes(position),seek(position + 1))
		} else {
			throw new IOException("read past EOF")
		}
	}
	/** Read byte at offset into bits at current if current is less than or equal to length
		* @param bits				array of bytes to write into
		* @param offset			position in file's bytes to read from
		* @param len				total number of bytes to read
		* @returns array of bytes written into while reading */
	def read(bits:Array[Byte],offset:Int,len:Int):DatastoreFile = {
		if (bits.length < len) throw new IOException("Array to read into is not large enough for the length specified")
		if (position + len > length) throw new IOException("read past EOF")
		val a = bytes.drop(position)
		for (i <- offset until (offset + len)) {
			bits(i) = a(i)
		}
		seek(position + len)
	}
	/** Move the position pointer to the new position
		* @param pos				new pointer position to set
		* @return file with the new pointer set */
	def seek(pos:Int):DatastoreFile = DatastoreFile(pos,bytes,ent)
	/** Move the position pointer to the new position
		* @param pos				new pointer position to set
		* @return file with the new pointer set
		* @throws IllegalArgumentException when pos is greater than scala.Math.MAX_INT */
	def seek(pos:Long):DatastoreFile = {
		if (pos > scala.Int.MaxValue) throw new IOException("pos is too large to be used as an array index")
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
			DatastoreFile(position + 1,bytes ::: List(b),ent)
		} else if (position > length) {
			DatastoreFile(position + 1,bytes ::: new Array[Byte](position - length).toList ::: List(b),ent)
		} else {
			DatastoreFile(position + 1,(bytes.take(position) ::: List(b) ::: bytes.drop(position + 1)),ent)
		}
	}
	/** Write the provided array of bytes into this file's contents
		* @param bits				array of bytes to be written to file
		* @param offset			position in file to start writing bytes
		* @param len				number of bytes to write
		* @return file with bits written to it */
	def write(bits:Array[Byte],offset:Int,len:Int):DatastoreFile =
		DatastoreFile(position + len,bytes.take(position) ::: bits.drop(offset).take(len).toList ::: bytes.drop(position + len),ent)
	/** Retrieve the underlying entity
		* @return the datastore representation which can be sent to storage */
	def entity:Entity = set(Contents,new Blob(bytes.toArray)).set(Size,length).set(Deleted,false).ent
}

object DatastoreFile {
	val Contents = "contents"
	val DateModified = "datemodified"
	val Deleted = "deleted"
	val Filename = "filename"
	val Kind = "DatastoreFile"
	val Size = "size"
	def apply():DatastoreFile = new DatastoreFile()
	def apply(entity:Entity):DatastoreFile = apply(0,entity)
	def apply(position:Int,entity:Entity):DatastoreFile = {
		if (!entity.hasProperty(Contents)) {
			new DatastoreFile(position,List[Byte](),entity)
		} else {
			new DatastoreFile(position,entity)
		}
	}
	def apply(position:Int,bytes:List[Byte],entity:Entity):DatastoreFile = new DatastoreFile(position,bytes,entity)
	def apply(filename:String):DatastoreFile = {
		val bytes = List[Byte]()
		val entity = new Entity(Kind,DatastoreDirectory.index)
		entity.setProperty(Filename,filename)
		entity.setProperty(Contents,new Blob(bytes.toArray))
		apply(entity)
	}
	def rename(file:DatastoreFile,to:String) = file.set(Filename,to)
}

sealed case class ByteAndFile(val byte:Byte,val file:DatastoreFile)
