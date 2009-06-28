package quotidian.model

import com.bryanjswift.persistence.{Savable}
import com.bryanjswift.persistence.annotations.{Entity,Persistent}
import java.io.Serializable
import quotidian.persistence.datastore.PersisterHelper
import scala.xml.NodeSeq

@Entity{val name = "Quote"}
class Quote(@Persistent val text:String, @Persistent val source:String, @Persistent val context:String) extends Savable {
	val id:Serializable = 0
	override def toString = {
		"Quote(" + List[String](text,source,context).mkString(",") + ")"
	}
	def toXml:NodeSeq = {
		<quote><text>{text}</text><source>{source}</source><context>{context}</context></quote>
	}
}

object Quote {
	val f = Quote.fromXml(_)
	PersisterHelper.register("Quote",f)
	def fromXml(xml:NodeSeq):Quote = {
		new Quote((xml \\ "text")(0).text,(xml \\ "source")(0).text,(xml \\ "context")(0).text)
	}
}