package quotidian

import java.util.logging.{Level,Logger}

trait Logging {
	lazy val log = Logger.getLogger(this.getClass.getName)
	def debug(m: => String) { if (log.isLoggable(Level.FINE)) log.fine(m) }
	def info(m: => String) { if (log.isLoggable(Level.INFO)) log.info(m) }
	def warn(m: => String) { if (log.isLoggable(Level.WARNING)) log.warning(m) }
	def error(m: => String) { if (log.isLoggable(Level.SEVERE)) log.severe(m) }
}
