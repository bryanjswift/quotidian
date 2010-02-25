import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.testing.{LocalDatastoreServiceTestConfig,LocalServiceTestHelper}
import java.io.File
import java.util.zip.{Checksum,CRC32}
import org.apache.lucene.index._
import org.apache.lucene.store._
import quotidian.search._

object DatastoreService {
	val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
	val service = LocalServiceTestHelper.getLocalService(LocalDatastoreService.PACKAGE).asInstanceOf[LocalDatastoreService]
	def start = {
		service.start
	}
	def stop = {
		service.clearProfiles
		service.stop
	}
	def setup(filename:String) = {
		val fsd = FSDirectory.getDirectory("target/index")
		val fso = fsd.createOutput(filename)
		val fsi = fsd.openInput(filename)
		val dsd = new DatastoreDirectory
		val dso = dsd.createOutput(filename)
		val dsi = dsd.openInput(filename)
		(fsd,fso,fsi,dsd,dso,dsi)
	}
}
