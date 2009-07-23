package quotidian

import java.util.logging.{Level,Logger}
import org.apache.velocity.runtime.{RuntimeServices => Services}
import org.apache.velocity.runtime.log.LogChute

class Log extends LogChute {
	protected val logger = Logger.getLogger(getClass().getName())
	def init(services:Services) = { }
	def isLevelEnabled(level:Int) = logger.isLoggable(level)
	def log(level:Int, message:String) = logger.log(level,message)
	def log(level:Int, message:String, t:Throwable) = logger.log(level,message,t)
	private implicit def intToLevel(l:Int):Level = {
		l match {
			case LogChute.ERROR_ID => Level.SEVERE
			case LogChute.WARN_ID => Level.WARNING
			case LogChute.INFO_ID => Level.INFO
			case LogChute.DEBUG_ID => Level.CONFIG
			case LogChute.TRACE_ID => Level.FINE
			case _ => Level.FINEST
		}
	}
}