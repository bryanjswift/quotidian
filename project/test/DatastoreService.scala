import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy
import com.google.apphosting.api.ApiProxy.Environment
import java.io.File
import java.util.zip.{Checksum,CRC32}
import org.apache.lucene.index._
import org.apache.lucene.store._
import quotidian.search._

object DatastoreService {
	class GaeEnvironment(private val requestNamespace:String) extends Environment {
		def this() = this("")
		def getAppId = "Quotidian Specs"
		def getVersionId = "1.0"
		def getRequestNamespace = requestNamespace
		def getAuthDomain = "gmail.com"
		def isLoggedIn = throw new UnsupportedOperationException()
		def getEmail = ""
		def isAdmin = throw new UnsupportedOperationException()
		def getAttributes = new java.util.HashMap[String,Object]()
	}
	val proxy = new ApiProxyLocalImpl(new File("./target/")){ }
	val service = proxy.getService("datastore_v3").asInstanceOf[LocalDatastoreService]
	def start = {
		proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY,true.toString)
		ApiProxy.setEnvironmentForCurrentThread(new GaeEnvironment)
		ApiProxy.setDelegate(proxy)
	}
	def stop = {
		service.clearProfiles
		proxy.stop
		service.stop
		ApiProxy.setDelegate(null)
		ApiProxy.setEnvironmentForCurrentThread(null)
		ApiProxy.clearEnvironmentForCurrentThread()
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
