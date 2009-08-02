package quotidian

import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy
import java.io.File
import org.specs.Specification

class DatastoreSpecification extends Specification {
	val proxy = new ApiProxyLocalImpl(new File("./target/")){ }
	proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY,true.toString)
	val service = proxy.getService("datastore_v3").asInstanceOf[LocalDatastoreService]
	def datastoreSetup:Unit = {
		ApiProxy.setEnvironmentForCurrentThread(new GaeEnvironment())
		ApiProxy.setDelegate(proxy)
	}
	def datastoreTeardown:Unit = {
		ApiProxy.setDelegate(null)
		ApiProxy.setEnvironmentForCurrentThread(null)
	}
	def datastoreCleanup:Unit = service.clearProfiles
	doBeforeSpec { datastoreSetup }
	doAfterSpec { datastoreTeardown }
}
