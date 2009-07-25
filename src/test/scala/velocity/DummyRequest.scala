package velocity

import java.io.{BufferedReader,IOException,UnsupportedEncodingException}
import java.security.Principal
import java.util.{Enumeration,Locale,Map}

import javax.servlet.{RequestDispatcher,ServletInputStream}
import javax.servlet.http.{Cookie,HttpServletRequest,HttpSession}

/**
 * @author bryanjswift
 */
trait DummyRequest extends HttpServletRequest {
	private[this] class UnsupportedMethodException extends RuntimeException
	def getAuthType():String = throw new UnsupportedMethodException()
	def getContextPath():String = throw new UnsupportedMethodException()
	def getCookies():Array[Cookie] = throw new UnsupportedMethodException()
	def getDateHeader(name:String):Long = throw new UnsupportedMethodException()
	def getHeader(name:String):String = throw new UnsupportedMethodException()
	def getHeaderNames():Enumeration[_] = throw new UnsupportedMethodException()
	def getHeaders(name:String):Enumeration[_] = throw new UnsupportedMethodException()
	def getIntHeader(name:String):Int = throw new UnsupportedMethodException()
	def getLocalAddr():String = throw new UnsupportedMethodException()
	def getLocalName():String = throw new UnsupportedMethodException()
	def getLocalPort():Int = throw new UnsupportedMethodException()
	def getMethod():String = throw new UnsupportedMethodException()
	def getPathInfo():String = throw new UnsupportedMethodException()
	def getPathTranslated():String = throw new UnsupportedMethodException()
	def getQueryString():String = throw new UnsupportedMethodException()
	def getRemotePort():Int = throw new UnsupportedMethodException()
	def getRemoteUser():String = throw new UnsupportedMethodException()
	def getRequestURI():String = throw new UnsupportedMethodException()
	def getRequestURL():StringBuffer = throw new UnsupportedMethodException()
	def getRequestedSessionId():String = throw new UnsupportedMethodException()
	def getServletPath():String = throw new UnsupportedMethodException()
	def getSession():HttpSession = throw new UnsupportedMethodException()
	def getSession(create:Boolean):HttpSession = throw new UnsupportedMethodException()
	def getUserPrincipal():Principal = throw new UnsupportedMethodException()
	def isRequestedSessionIdFromCookie():Boolean = throw new UnsupportedMethodException()
	def isRequestedSessionIdFromURL():Boolean = throw new UnsupportedMethodException()
	def isRequestedSessionIdFromUrl():Boolean = throw new UnsupportedMethodException()
	def isRequestedSessionIdValid():Boolean = throw new UnsupportedMethodException()
	def isUserInRole(role:String):Boolean = throw new UnsupportedMethodException()
	def getAttribute(name:String):Object = throw new UnsupportedMethodException()
	def getAttributeNames():Enumeration[_] = throw new UnsupportedMethodException()
	def getCharacterEncoding():String = throw new UnsupportedMethodException()
	def getContentLength():Int = throw new UnsupportedMethodException()
	def getContentType():String = throw new UnsupportedMethodException()
	def getInputStream():ServletInputStream = throw new UnsupportedMethodException()
	def getLocale():Locale = throw new UnsupportedMethodException()
	def getLocales():Enumeration[_] = throw new UnsupportedMethodException()
	def getParameter(name:String):String = throw new UnsupportedMethodException()
	def getParameterMap():Map[_,_] = throw new UnsupportedMethodException()
	def getParameterNames():Enumeration[_] = throw new UnsupportedMethodException()
	def getParameterValues(name:String):Array[String] = throw new UnsupportedMethodException()
	def getProtocol():String = throw new UnsupportedMethodException()
	def getReader():BufferedReader = throw new UnsupportedMethodException()
	def getRealPath(path:String):String = throw new UnsupportedMethodException()
	def getRemoteAddr():String = throw new UnsupportedMethodException()
	def getRemoteHost():String = throw new UnsupportedMethodException()
	def getRequestDispatcher(path:String):RequestDispatcher = throw new UnsupportedMethodException()
	def getScheme():String = throw new UnsupportedMethodException()
	def getServerName():String = throw new UnsupportedMethodException()
	def getServerPort():Int = throw new UnsupportedMethodException()
	def isSecure():Boolean = throw new UnsupportedMethodException()
	def removeAttribute(name:String):Unit = throw new UnsupportedMethodException()
	def setAttribute(name:String, o:Any):Unit = throw new UnsupportedMethodException()
	def setCharacterEncoding(arg0:String):Unit = throw new UnsupportedMethodException()
}
