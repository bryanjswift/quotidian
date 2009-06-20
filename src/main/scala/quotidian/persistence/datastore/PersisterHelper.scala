package quotidian.persistence.datastore

import com.bryanjswift.persistence.Savable
import com.google.appengine.api.datastore.Entity
import scala.collection.mutable.Map
import scala.xml.{NodeSeq,XML}

object PersisterHelper {
	var mapper = Map[String,Function1[NodeSeq,Savable]]()
	def toXml(entity:Entity):NodeSeq = {
		val properties = entity.getProperties	
		val keys = Set(properties.keySet)
		val nodes = for {
			val key <- keys
		} yield "<" + key + ">" + properties.get(key) + "</" + key + ">"
		val s = nodes.mkString("<" + entity.getKind() + ">","","</" + entity.getKind() + ">")
		XML.load(s)
	}
}
