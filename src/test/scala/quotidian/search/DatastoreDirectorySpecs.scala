package quotidian.search

import quotidian.DatastoreSpecification

object DatastoreDirectorySpecs extends DatastoreSpecification {
	"A new DatastoreDirectory" should {
		datastoreCleanup.before
		val directory = new DatastoreDirectory
		"contain no files" >> {
			0 mustEqual directory.list.length
		}
		"be able to create new files" >> {
			val output = directory.createOutput("test")
			val bytes = "This is a string to write".getBytes
			bytes.length must be_==(25)
			output.writeBytes(bytes,0,bytes.length)
			output.flush
			directory.fileExists("test") must be(true)
		}
	}
}
