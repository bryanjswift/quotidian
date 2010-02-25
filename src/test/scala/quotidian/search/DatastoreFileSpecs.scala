package quotidian.search

import com.google.appengine.api.datastore.Blob
import quotidian.DatastoreSpecification

class DatastoreFileSpecs extends DatastoreSpecification {
	"An empty file" should {
		val byte = 15.toByte
		"have a position at the start" >> {
			val file = DatastoreFile()
			file.position mustEqual 0
		}
		"have zero bytes" >> {
			val file = DatastoreFile()
			file.length mustEqual 0
		}
		"be able to write bytes" >> {
			val file = DatastoreFile()
			file.write(byte).length mustEqual 1
		}
		"be able to read a written byte" >> {
			val file = DatastoreFile().write(byte)
			file.seek(0).read.byte mustEqual byte
		}
		"be able to read a written byte into an array" >> {
			val file = DatastoreFile().write(byte)
			val bits = new Array[Byte](1)
			file.seek(0).read(bits,0,1)
			bits(0) mustEqual byte
		}
	}
	"A file with bytes written" should {
		val ints = (0 until 15)
		val bytes = (for (i <- ints) yield i.asInstanceOf[Byte]).toArray
		"be able to read all written bytes" >> {
			val file = DatastoreFile()
			val bits = new Array[Byte](15)
			file.write(bytes,0,15).seek(0).read(bits,0,15)
			bits must containInOrder(bytes)
		}
		"provide an entity with the written bytes contained" >> {
			val file = DatastoreFile().write(bytes,0,15)
			val bits = file.entity.getProperty(DatastoreFile.Contents).asInstanceOf[Blob].getBytes
			bits must containInOrder(bytes)
		}
		"have a length equal to the number of bytes written" >> {
			val file = DatastoreFile().write(bytes,0,15)
			file.length must beGreaterThanOrEqualTo(bytes.length)
		}
		"be able to seek within the bytes written" >> {
			val file = DatastoreFile().write(bytes,0,15)
			file.seek(bytes.length - 5).position mustEqual(bytes.length - 5)
		}
		"be able to seek outside the bytes written" >> {
			val file = DatastoreFile().write(bytes,0,15)
			file.seek(bytes.length + 5).position mustEqual(bytes.length + 5)
		}
		"be able to write in the middle of the file" >> {
			val file = DatastoreFile().write(bytes,0,15)
			val written = file.seek(5).write(bytes,10,3)
			written.seek(4).read.byte mustEqual bytes(4)
			written.seek(5).read.byte mustEqual bytes(10)
			written.seek(6).read.byte mustEqual bytes(11)
			written.seek(7).read.byte mustEqual bytes(12)
			written.seek(8).read.byte mustEqual bytes(8)
		}
	}
}
