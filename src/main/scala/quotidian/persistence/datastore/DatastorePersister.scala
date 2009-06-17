package quotidian.persistence.datastore

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity}
import com.bryanjswift.persistence.{Persister,Savable}
import java.io.Serializable

object DatastorePersister extends Persister {
	val datastore = DatastoreServiceFactory.getDatastoreService()
	def save(obj:Savable):Serializable = {
		val entity = if (obj.id == 0) new Entity(obj.table) else new Entity(obj.table,obj.id.toString)
		val map = obj.fields zip obj.values
		map.foreach(t => {
			entity.setProperty(t._1,t._2)
		})
		datastore.put(entity)
	}
}
