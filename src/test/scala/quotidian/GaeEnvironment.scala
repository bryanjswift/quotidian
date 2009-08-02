package quotidian

import com.google.apphosting.api.ApiProxy.Environment

class GaeEnvironment() extends Environment {
	def getAppId = "Quotidian Specs"
	def getVersionId = "1.0"
	def getRequestNamespace = "gmail.com"
	def getAuthDomain = "gmail.com"
	def isLoggedIn = false
	def getEmail = ""
	def isAdmin = false
	def getAttributes = new java.util.HashMap[String,Object]()
}
