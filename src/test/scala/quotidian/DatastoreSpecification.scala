package quotidian

import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy
import com.google.apphosting.api.ApiProxy.Environment
import java.io.File
import org.specs.Specification

class DatastoreSpecification extends Specification {
	val directoryName = "target/index"
	val proxy = new ApiProxyLocalImpl(new File("./target/")){ }
	proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY,true.toString)
	val service = proxy.getService("datastore_v3").asInstanceOf[LocalDatastoreService]
	private class GaeEnvironment(private val requestNamespace:String) extends Environment {
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
	def datastoreSetup:Unit = {
		val env = new GaeEnvironment
		ApiProxy.setEnvironmentForCurrentThread(env)
		ApiProxy.setDelegate(proxy)
	}
	def datastoreTeardown:Unit = {
		ApiProxy.setDelegate(null)
		ApiProxy.setEnvironmentForCurrentThread(null)
	}
	def datastoreCleanup:Unit = service.clearProfiles
	def filesystemCleanup:Unit = {
		val file = new File(directoryName)
		if (file.exists) file.delete
	}
	doBeforeSpec { datastoreSetup }
	doAfterSpec { datastoreTeardown }
}

