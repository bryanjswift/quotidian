package velocity

import java.io.{IOException,PrintWriter}
import java.util.Locale

import javax.servlet.ServletOutputStream
import javax.servlet.http.{Cookie,HttpServletResponse}


trait DummyResponse extends HttpServletResponse {
	private[this] class UnsupportedMethodException extends RuntimeException
	def addCookie(cookie:Cookie):Unit = throw new UnsupportedMethodException()
	def addDateHeader(name:String,date:Long):Unit = throw new UnsupportedMethodException()
	def addHeader(name:String,value:String):Unit = throw new UnsupportedMethodException()
	def addIntHeader(name:String,value:Int):Unit = throw new UnsupportedMethodException()
	def containsHeader(name:String):Boolean = throw new UnsupportedMethodException()
	def encodeRedirectURL(url:String):String = throw new UnsupportedMethodException()
	def encodeRedirectUrl(url:String):String = throw new UnsupportedMethodException()
	def encodeURL(url:String):String = throw new UnsupportedMethodException()
	def encodeUrl(url:String):String = throw new UnsupportedMethodException()
	def sendError(sc:Int):Unit = throw new UnsupportedMethodException()
	def sendError(sc:Int,msg:String):Unit = throw new UnsupportedMethodException()
	def sendRedirect(location:String):Unit = throw new UnsupportedMethodException()
	def setDateHeader(name:String,date:Long):Unit = throw new UnsupportedMethodException()
	def setHeader(name:String,value:String):Unit = throw new UnsupportedMethodException()
	def setIntHeader(name:String,value:Int):Unit = throw new UnsupportedMethodException()
	def setStatus(sc:Int):Unit = throw new UnsupportedMethodException()
	def setStatus(sc:Int,sm:String):Unit = throw new UnsupportedMethodException()
	def flushBuffer():Unit = throw new UnsupportedMethodException()
	def getBufferSize():Int = throw new UnsupportedMethodException()
	def getCharacterEncoding():String = throw new UnsupportedMethodException()
	def getContentType():String = throw new UnsupportedMethodException()
	def getLocale():Locale = throw new UnsupportedMethodException()
	def getOutputStream():ServletOutputStream = throw new UnsupportedMethodException()
	def getWriter():PrintWriter = throw new UnsupportedMethodException()
	def isCommitted():Boolean = throw new UnsupportedMethodException()
	def reset():Unit = throw new UnsupportedMethodException()
	def resetBuffer():Unit = throw new UnsupportedMethodException()
	def setBufferSize(size:Int):Unit = throw new UnsupportedMethodException()
	def setContentLength(len:Int):Unit = throw new UnsupportedMethodException()
	def setContentType(contentType:String):Unit = throw new UnsupportedMethodException()
	def setCharacterEncoding(ce:String):Unit = throw new UnsupportedMethodException()
	def setLocale(loc:Locale):Unit = throw new UnsupportedMethodException()
}
