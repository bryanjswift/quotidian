package quotidian

import com.google.apphosting.api.ApiProxy.Environment

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
