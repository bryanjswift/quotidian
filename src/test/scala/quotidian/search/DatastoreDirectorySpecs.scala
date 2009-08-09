package quotidian.search

import java.util.Calendar
import quotidian.DatastoreSpecification

object DatastoreDirectorySpecs extends DatastoreSpecification {
	"A DatastoreDirectory" should {
		val specsStart = Calendar.getInstance.getTimeInMillis
		val directory = new DatastoreDirectory
		val filename = "test"
		val bytes = "This is a string to write".getBytes
		"contain no files" >> {
			0 mustEqual directory.list.length
		}
		"be able to create new files" >> {
			val output = directory.createOutput(filename)
			output.writeBytes(bytes,0,bytes.length)
			output.flush
			directory.fileExists("test") must be(true)
		}
		"be able to read existing files" >> {
			val input = directory.openInput(filename)
			input.readByte mustEqual bytes(0)
		}
		"be able to rename existing files" >> {
			val renamed = "wilbur"
			directory.renameFile(filename,renamed)
			directory.fileExists(renamed) must be(true)
			directory.fileExists(filename) must be(false)
			directory.renameFile(renamed,filename)
			directory.fileExists(filename) must be(true)
			directory.fileExists(renamed) must be(false)
		}
		"be able to check the fileLength of files" >> {
			bytes.length mustEqual directory.fileLength(filename)
		}
		"be able to check the last modified date of a file" >> {
			val modified = directory.fileModified(filename)
			val now = Calendar.getInstance.getTimeInMillis
			specsStart must beLessThan(modified)
			modified must beLessThan(now)
		}
		"be able to list file names" >> {
			val filenames = directory.list
			1 mustEqual filenames.length
			filenames mustContain(filename)
		}
	}
}

