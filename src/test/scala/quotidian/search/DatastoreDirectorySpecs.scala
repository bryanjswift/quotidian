package quotidian.search

import java.util.Calendar
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.document.{Document,Field}
import org.apache.lucene.index.{IndexReader,IndexWriter}
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import quotidian.DatastoreSpecification

class DatastoreDirectorySpecs extends DatastoreSpecification {
	val docsToAdd = 500
	def setup = {
		val dir = new DatastoreDirectory
		val writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED)
		for (i <- 0 until docsToAdd) {
			val doc = new Document
			doc.add(new Field("content", English.intToEnglish(i).trim, Field.Store.YES, Field.Index.NOT_ANALYZED))
			writer.addDocument(doc)
		}
		writer.maxDoc mustEqual docsToAdd
		writer.close
		dir
	}
	"A new DatastoreDirectory" should {
		shareVariables()
		doLast {
			datastoreCleanup
		}
		val specsStart = Calendar.getInstance.getTimeInMillis
		val directory = new DatastoreDirectory
		val filename = "test"
		val bytes = "This is a string to write".getBytes
		"contain no files" >> {
			0 mustEqual directory.listAll.length
		}
		"be able to create new files" >> {
			val output = directory.createOutput(filename)
			output.writeBytes(bytes,0,bytes.length)
			output.close
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
			directory.fileLength(filename).toInt must beGreaterThanOrEqualTo(bytes.length)
		}
		"be able to check the last modified date of a file" >> {
			val modified = directory.fileModified(filename)
			val now = Calendar.getInstance.getTimeInMillis
			specsStart must beLessThan(modified)
			modified must beLessThan(now)
		}
		"be able to list file names" >> {
			val filenames = directory.listAll
			1 mustEqual filenames.length
			filenames.toList mustContain(filename)
		}
	}
	"A setup Directory" should {
		datastoreCleanup.after
		"have readable documents" >> {
			val dir = setup
			val reader = IndexReader.open(dir)
			reader.numDocs mustEqual docsToAdd
			val searcher = new IndexSearcher(reader)
			for (i <- 0 until docsToAdd) {
				val doc = searcher.doc(i)
				val field = doc.getField("content")
				field must notBeNull
				field.stringValue mustEqual English.intToEnglish(i).trim
			}
			reader.close
			searcher.close
		}
		"have documents with terms" >> {
			val dir = setup
			val reader = IndexReader.open(dir)
			val terms = reader.terms
			terms.next mustEqual true
			reader.close
		}
		"have a searcher with a reader with terms" >> {
			val dir = setup
			val searcher = new IndexSearcher(dir)
			val reader = searcher.getIndexReader
			val terms = reader.terms
			terms.next mustEqual true
			reader.close
		}
	}
}

object English {
	def intToEnglish(in:Int):String = {
		in match {
			case i if (i == 0) => "zero"
			case i if (i < 0) => "minus " + intToEnglish(i)
			case i if (i >= 1000000000) => intToEnglish(i / 1000000000) + "billion, " + intToEnglish(i % 1000000000)
			case i if (i >= 1000000) => intToEnglish(i / 1000000) + "million, " + intToEnglish(i % 1000000)
			case i if (i >= 1000) => intToEnglish(i / 1000) + "thousand, " + intToEnglish(i % 1000)
			case i if (i >= 100) => intToEnglish(i / 100) + "hundred, " + intToEnglish(i % 100)
			case i if (i >= 20) => {
				val j = i / 10
				val tmp = j match {
					case 9 => "ninety"
					case 8 => "eighty"
					case 7 => "seventy"
					case 6 => "sixty"
					case 5 => "fifty"
					case 4 => "forty"
					case 3 => "thirty"
					case 2 => "twenty"
				}
				val c = if (i % 10 == 0) " " else "-" + intToEnglish(i % 10)
				tmp + c
			}
			case 19 => "nineteen"
			case 18 => "eighteen"
			case 17 => "seventeen"
			case 16 => "sixteen" 
			case 15 => "fifteen"
			case 14 => "fourteen"
			case 13 => "thirteen"
			case 12 => "twelve"
			case 11 => "eleven"
			case 10 => "ten"
			case 9 => "nine"
			case 8 => "eight"
			case 7 => "seven"
			case 6 => "six"
			case 5 => "five"
			case 4 => "four"
			case 3 => "three"
			case 2 => "two"
			case 1 => "one"
			case _ => ""
		}
	}
}
