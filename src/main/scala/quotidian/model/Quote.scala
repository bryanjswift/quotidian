package quotidian.model

import basic.persistence.{Savable}
import basic.persistence.annotations.{Entity,Persistent}
import java.io.Serializable
import quotidian.Logging
import quotidian.persistence.datastore.PersisterHelper
import scala.xml.NodeSeq

@Entity{val name = "Quote"}
class Quote(
	val id:Serializable,
	@Persistent val text:String,
	@Persistent val source:String,
	@Persistent val context:String
) extends Savable {
	def this(text:String,source:String,context:String) = this(0,text,source,context)
	def canEqual(a:Any) = a.isInstanceOf[Quote]
	def equals(q:Quote) = {
		this.text == q.text && this.source == q.source && this.context == q.context
	}
	override def equals(q:Any) = q match {
		case that:Quote => this.canEqual(q) && equals(that)
		case _ => false
	}
	override def hashCode = 41 * (41 * (41 + text.hashCode) + source.hashCode) + context.hashCode
	override def toString = {
		"Quote(" + List[String](text,source,context).mkString(",") + ")"
	}
	def toXml:NodeSeq = {
		<quote><text>{text}</text><source>{source}</source><context>{context}</context></quote>
	}
}

object Quote extends Logging {
	val kind = "Quote"
	private val f = Quote.fromXml(_)
	PersisterHelper.register(kind,f)
	def apply(text:String,source:String,context:String) = new Quote(text,source,context)
	def apply(id:Serializable,text:String,source:String,context:String) = new Quote(id,text,source,context)
	def apply(xml:NodeSeq):Quote = fromXml(xml)
	def fromXml(xml:NodeSeq):Quote = new Quote((xml \\ "text")(0).text,(xml \\ "source")(0).text,(xml \\ "context")(0).text)
}
