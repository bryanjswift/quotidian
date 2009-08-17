package quotidian.search

import java.io.IOException
import java.util.Calendar
import org.apache.lucene.store.{FSDirectory,IndexInput,IndexOutput}
import quotidian.DatastoreSpecification

object DatastoreIndexInputSpecs extends DatastoreSpecification {
	val filename = "1.fdx"
	val length = 40
	val rand = new scala.util.Random(System.currentTimeMillis)
	def prepareOutput(output:IndexOutput,length:Int) = {
		val bits = new Array[Byte](length)
		rand.nextBytes(bits)
		output.writeBytes(bits,0,length)
		(output,bits)
	}
	"A DatastoreIndexInput" should {
		var fsd:FSDirectory = null
		var fso:IndexOutput = null
		var fsi:IndexInput = null
		var dsd:DatastoreDirectory = null
		var dso:IndexOutput = null
		var dsi:IndexInput = null
		doBefore {
			datastoreCleanup
			filesystemCleanup
			fsd = FSDirectory.getDirectory(directoryName)
			fso = fsd.createOutput(filename)
			fsi = fsd.openInput(filename)
			dsd = new DatastoreDirectory
			dso = dsd.createOutput(filename)
			dsi = dsd.openInput(filename)
		}
		"throw an EOF error when reading while file pointer and length are the same" >> {
			dsi.readByte must throwA[IOException]
		}
		"throw an EOF error when trying to read after writing if input not refreshed" >> {
			prepareOutput(dso,length)._1.close
			dsi.readByte must throwA[IOException]
		}
		"throw an EOF error when trying to read too many bytes" >> {
			prepareOutput(dso,length)._1.close
			dsi = dsd.openInput(filename)
			val bits = new Array[Byte](length * 2)
			dsi.readBytes(bits,0,length + 1) must throwA[IOException]
		}
		"be able to read all written bytes" >> {
			val oAndB = prepareOutput(dso,length)
			oAndB._1.close
			dsi = dsd.openInput(filename)
			val bytes = oAndB._2
			val bits = new Array[Byte](bytes.length)
			dsi.readBytes(bits,0,length)
			bits must containInOrder(bytes)
		}
	}
}
