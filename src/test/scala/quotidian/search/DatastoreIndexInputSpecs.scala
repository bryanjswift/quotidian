package quotidian.search

import com.google.appengine.api.datastore.{Blob,Entity}
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
			fsd = FSDirectory.getDirectory(directoryName)
			fso = fsd.createOutput(filename)
			dsd = new DatastoreDirectory
			dso = dsd.createOutput(filename)
		}
		doAfter {
			datastoreCleanup
			filesystemCleanup
		}
		"throw an EOF error when reading while file pointer and length are the same" >> {
			dsi = dsd.openInput(filename)
			dsi.readByte must throwA[IOException]
		}
		"throw an EOF error when trying to read after writing if input not refreshed" >> {
			dsi = dsd.openInput(filename)
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
		"read as any other IndexInput" >> {
			implicit def i2b(i:Int) = i.toByte
			val bytes = List[Byte](
				0x80, 0x01,
				0xFF, 0x7F,
				0x80, 0x80, 0x01,
				0x81, 0x80, 0x01,
				0x06, 'L', 'u', 'c', 'e', 'n', 'e',
				// 2-byte UTF-8 (U+00BF "INVERTED QUESTION MARK") 
				0x02, 0xC2, 0xBF,
				0x0A, 'L', 'u', 0xC2, 0xBF, 
							'c', 'e', 0xC2, 0xBF, 
							'n', 'e',

				// 3-byte UTF-8 (U+2620 "SKULL AND CROSSBONES") 
				0x03, 0xE2, 0x98, 0xA0,
				0x0C, 'L', 'u', 0xE2, 0x98, 0xA0,
							'c', 'e', 0xE2, 0x98, 0xA0,
							'n', 'e',

				// surrogate pairs
				// (U+1D11E "MUSICAL SYMBOL G CLEF")
				// (U+1D160 "MUSICAL SYMBOL EIGHTH NOTE")
				0x04, 0xF0, 0x9D, 0x84, 0x9E,
				0x08, 0xF0, 0x9D, 0x84, 0x9E, 
							0xF0, 0x9D, 0x85, 0xA0, 
				0x0E, 'L', 'u',
							0xF0, 0x9D, 0x84, 0x9E,
							'c', 'e', 
							0xF0, 0x9D, 0x85, 0xA0, 
							'n', 'e',	

				// null bytes
				0x01, 0x00,
				0x08, 'L', 'u', 0x00, 'c', 'e', 0x00, 'n', 'e'
				
				// Modified UTF-8 null bytes
//				0x02, 0xC0, 0x80,
//				0x0A, 'L', 'u', 0xC0, 0x80, 
//							'c', 'e', 0xC0, 0x80, 
//							'n', 'e'
			)
			val entity = new Entity(DatastoreFile.Kind)
			entity.setProperty(DatastoreFile.Filename,filename)
			entity.setProperty(DatastoreFile.Contents,new Blob(bytes.toArray))

			dsi = new DatastoreIndexInput(dsd,DatastoreFile(entity))
			128 mustEqual dsi.readVInt
			16383 mustEqual dsi.readVInt
			16384 mustEqual dsi.readVInt
			16385 mustEqual dsi.readVInt
			"Lucene" mustEqual dsi.readString
			// 2-byte UTF-8 (U+00BF "INVERTED QUESTION MARK") 
			"\u00BF" mustEqual dsi.readString
			"Lu\u00BFce\u00BFne" mustEqual dsi.readString
			// 3-byte UTF-8 (U+2620 "SKULL AND CROSSBONES") 
			"\u2620" mustEqual dsi.readString
			"Lu\u2620ce\u2620ne" mustEqual dsi.readString
			// surrogate pairs
			// (U+1D11E "MUSICAL SYMBOL G CLEF")
			// (U+1D160 "MUSICAL SYMBOL EIGHTH NOTE")
			"\uD834\uDD1E" mustEqual dsi.readString
			"\uD834\uDD1E\uD834\uDD60" mustEqual dsi.readString
			"Lu\uD834\uDD1Ece\uD834\uDD60ne" mustEqual dsi.readString
			// null bytes
			"\u0000" mustEqual dsi.readString
			"Lu\u0000ce\u0000ne" mustEqual dsi.readString
			// Modified UTF-8 null bytes
//			"\u0000" mustEqual dsi.readString
//			"Lu\u0000ce\u0000ne" mustEqual dsi.readString
		}
	}
}
