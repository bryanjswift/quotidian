package quotidian.search

import com.google.appengine.api.datastore.Blob
import quotidian.DatastoreSpecification

object DatastoreFileSpecs extends DatastoreSpecification {
	"An empty file" should {
		val byte = 15.toByte
		"have a position at the start" >> {
			val file = DatastoreFile()
			file.position mustEqual 0
		}
		"have a single zero byte" >> {
			val file = DatastoreFile()
			val bf = file.read
			bf.byte mustEqual 0
			file.length mustEqual 1
		}
		"be able to read beyond it's current capacity without error" >> {
			val file = DatastoreFile()
			val bf = file.read.file.read
			bf.file.length mustEqual 3
		}
		"be able to write bytes" >> {
			val file = DatastoreFile()
			file.position mustEqual 0
			val bits = new Array[Byte](1)
			file.write(byte).read(bits,0,1)
			bits(0) mustEqual byte
		}
	}
	"A file with bytes written" should {
		val ints = (0 until 15)
		val bytes = (for (i <- ints) yield i.asInstanceOf[Byte]).toArray
		"be able to read all written bytes" >> {
			val file = DatastoreFile()
			val bits = new Array[Byte](15)
			file.write(bytes,0,15).read(bits,0,15)
			bits must containInOrder(bytes)
		}
		"provide an entity with the written bytes contained" >> {
			val file = DatastoreFile().write(bytes,0,15)
			val bits = file.entity.getProperty(DatastoreFile.Contents).asInstanceOf[Blob].getBytes
			bits must containInOrder(bytes)
		}
		"have a length equal to the number of bytes written" >> {
			val file = DatastoreFile().write(bytes,0,15)
			file.length mustEqual bytes.length
		}
		"be able to seek within the bytes written" >> {
			val file = DatastoreFile().write(bytes,0,15)
			file.seek(bytes.length - 5).position mustEqual(bytes.length - 5)
		}
		"be able to seek outside the bytes written" >> {
			val file = DatastoreFile().write(bytes,0,15)
			file.seek(bytes.length + 5).position mustEqual(bytes.length + 5)
		}
	}
}
