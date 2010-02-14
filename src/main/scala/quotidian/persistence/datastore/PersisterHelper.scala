package quotidian.persistence.datastore

import basic.persistence.Savable
import com.google.appengine.api.datastore.Entity
import quotidian.Logging
import scala.collection.JavaConversions
import scala.xml.{NodeSeq,XML}

object PersisterHelper extends Logging {
	private var mapper = Map[String,Function1[NodeSeq,Savable]]()
	def toXml(entity:Entity):NodeSeq = {
		val properties = entity.getProperties	
		val keys = JavaConversions.asSet(properties.keySet)
		val id = "<id>" + entity.getKey.getId + "</id>"
		val nodes = List(id) ++ (for {
			key <- keys
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
}
