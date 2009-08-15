package quotidian.persistence.datastore

import basic.persistence.Savable
import com.google.appengine.api.datastore.Entity
import quotidian.Logging
import quotidian.model.Quote
import scala.collection.jcl.Conversions._
import scala.xml.{NodeSeq,XML}

object PersisterHelper extends Logging {
	private var mapper = Map[String,Function1[NodeSeq,Savable]]()
	def toXml(entity:Entity):NodeSeq = {
		val properties = entity.getProperties	
		val keys = properties.keySet
		val nodes = List("<id>" + entity.getKey.getId + "</id>") ++ (for {
			val key <- keys
		} yield "<" + key + ">" + properties.get(key) + "</" + key + ">")
		val s = nodes.mkString("<" + entity.getKind() + ">","","</" + entity.getKind() + ">")
		XML.loadString(s)
	}
	def register(table:String,fcn:Function1[NodeSeq,Savable]):Unit = {
		mapper = mapper + (table -> fcn)
	}
	def fetch(table:String):Function1[NodeSeq,Savable] = {
		mapper.get(table) match {
			case Some(fcn) => fcn
			case None => null
		}
	}
	def entity2quote(entity:Entity):Quote = {
		val e = new RichEntity(entity)
		Quote(e.id,e.get[String]("text"),e.get[String]("source"),e.get[String]("context"))
	}
}

class RichEntity(private val entity:Entity) {
	def get[T <: AnyRef](property:String):T = entity.getProperty(property).asInstanceOf[T]
	def id = entity.getKey.getId
}

