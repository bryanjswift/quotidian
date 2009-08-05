import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy
import com.google.apphosting.api.ApiProxy.Environment
import java.io.File
import quotidian.GaeEnvironment

val proxy = new ApiProxyLocalImpl(new File("./target/")){ }
proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY,true.toString)
val service = proxy.getService("datastore_v3").asInstanceOf[LocalDatastoreService]
val env = new GaeEnvironment
ApiProxy.setEnvironmentForCurrentThread(env)
ApiProxy.setDelegate(proxy)
