package quotidian

import java.util.logging.Logger

trait Logging {
	private lazy val log = Logger.getLogger(getClass().getName())
	def info(m:String):Unit = { log.info(m) }
	def warn(m:String):Unit = { log.warning(m) }
}
