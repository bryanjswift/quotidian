package quotidian

import com.google.appengine.tools.development.testing.{LocalDatastoreServiceTestConfig,LocalServiceTestHelper}
import java.io.File
import org.specs.Specification

class DatastoreSpecification extends Specification {
	val directoryName = "target/index"
	val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
	def datastoreSetup:Unit = {
		helper.setUp
	}
	def datastoreTeardown:Unit = {
		helper.tearDown
	}
	def datastoreCleanup:Unit = {
		helper.tearDown
		helper.setUp
	}
	def filesystemCleanup:Unit = {
		val file = new File(directoryName)
		if (file.exists) file.delete
	}
	doBeforeSpec { datastoreSetup }
	doAfterSpec { datastoreTeardown }
}

