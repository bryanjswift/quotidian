package quotidian

import java.util.Properties

object ConfigFactory {
	private[this] val properties = new Properties()
	properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"))
	def apply(property:String) = properties.getProperty(property)
	def objectForProperty[T](property:String) = Class.forName(apply(property)).getConstructor().newInstance().asInstanceOf[T]
}
