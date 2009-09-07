package quotidian.model

import basic.persistence.{Savable}
import basic.persistence.annotations.{Entity,Persistent}
import java.io.Serializable
import org.apache.lucene.document.{Document,Field}
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
	private lazy val all = List(text,source,context)
	private def canEqual(a:Any) = a.isInstanceOf[Quote]
	def equals(q:Quote) = {
		this.text == q.text && this.source == q.source && this.context == q.context
	}
	override def equals(q:Any) = q match {
		case that:Quote =>
			canEqual(q) && equals(that)
		case _ => false
	}
	override def hashCode = 41 * (41 * (41 + text.hashCode) + source.hashCode) + context.hashCode
	override def toString = {
		Quote.Kind + all.mkString("(",",",")")
	}
}

object Quote extends Logging {
	val Context = "context"
	val Id = "id"
	val Kind = "Quote"
	val Source = "source"
	val Text = "text"
	PersisterHelper.register(Kind,fromXml)
	def apply(text:String,source:String,context:String) = new Quote(text,source,context)
	def apply(id:Serializable,text:String,source:String,context:String) = new Quote(id,text,source,context)
	def apply(xml:NodeSeq):Quote = fromXml(xml)
	private def fromXml(xml:NodeSeq):Quote = {
		new Quote((xml \\ Id)(0).text,(xml \\ Text)(0).text,(xml \\ Source)(0).text,(xml \\ Context)(0).text)
	}
	implicit def quote2document(quote:Quote):Document = {
		val document = new Document()
		document.add(new Field(Id,quote.id.toString,Field.Store.YES,Field.Index.NO))
		document.add(new Field(Text,quote.text,Field.Store.YES,Field.Index.ANALYZED))
		document.add(new Field(Source,quote.source,Field.Store.YES,Field.Index.NOT_ANALYZED))
		document.add(new Field(Context,quote.context,Field.Store.YES,Field.Index.NOT_ANALYZED))
		document
	}
}
