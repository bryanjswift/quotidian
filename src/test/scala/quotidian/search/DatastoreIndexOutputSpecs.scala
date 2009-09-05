package quotidian.search

import java.util.Calendar
import org.apache.lucene.store.{FSDirectory,IndexInput,IndexOutput}
import quotidian.DatastoreSpecification

object DatastoreIndexOutputSpecs extends DatastoreSpecification {
	val filename = "1.fdx"
	val rand = new scala.util.Random(System.currentTimeMillis)
	"A DatastoreIndexOutput" should {
		var fsd:FSDirectory = null
		var fso:IndexOutput = null
		var fsi:IndexInput = null
		var dsd:DatastoreDirectory = null
		var dso:IndexOutput = null
		var dsi:IndexInput = null
		doBefore {
			fsd = FSDirectory.getDirectory(directoryName)
			fso = fsd.createOutput(filename)
			fsi = fsd.openInput(filename)
			dsd = new DatastoreDirectory
			dso = dsd.createOutput(filename)
			dsi = dsd.openInput(filename)
		}
		doAfter {
			datastoreCleanup
			filesystemCleanup
		}
		"have the same position as FSIndexOuput after writing the same data" >> {
			fso.getFilePointer mustEqual dso.getFilePointer
			val i = rand.nextInt
			fso.writeInt(i)
			dso.writeInt(i)
			fso.getFilePointer mustEqual dso.getFilePointer
		}
	}
}
