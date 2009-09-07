package quotidian.web.controller

import org.apache.lucene.document.Document

class DocumentWrapper(private val document:Document) {
	def apply(name:String):Option[String] =
		try { Some(document.getValues(name)(0)) }
		catch {
			case _ => None
		}
}

object DocumentWrapper {
	def apply(doc:Document) = new DocumentWrapper(doc)
	implicit def document2documentWrapper(doc:Document) = DocumentWrapper(doc)
}

