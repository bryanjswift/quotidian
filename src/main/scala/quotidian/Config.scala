package quotidian

import java.util.Properties
import quotidian.model.{Quote,Search}
import quotidian.web.controller.{QuoteController,SearchController}

object Config {
	private[this] val properties = new Properties()
	properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"))
	val Labels = Map(
		Quote.Text -> "*Words to remember...",
		Quote.Source -> "The wordsmith..",
		Quote.Context -> "Provide some context..",
		Search.Key -> "Find some words..")
	val IndexQuoteQueue = "index-quote"
	def apply(property:String) = properties.getProperty(property)
	def objectForProperty[T](property:String) = Class.forName(apply(property)).getConstructor().newInstance().asInstanceOf[T]
	val qc = objectForProperty[QuoteController]("quote.controller")
	val sc = objectForProperty[SearchController]("search.controller")
}
