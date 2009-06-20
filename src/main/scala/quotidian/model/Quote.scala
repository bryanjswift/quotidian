package quotidian.model

import com.bryanjswift.persistence.{Savable}
import com.bryanjswift.persistence.annotations.{Entity,Persistent}
import java.io.Serializable
import quotidian.persistence.datastore.PersisterHelper
import scala.xml.NodeSeq

@Entity{val name = "Quote"}
class Quote(@Persistent val text:String, @Persistent val source:String, @Persistent val context:String) extends Savable {
	val id:Serializable = 0
}
