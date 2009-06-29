package quotidian.persistence.datastore

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,Key,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withLimit}
import com.bryanjswift.persistence.{Persister,Savable}
import java.io.Serializable
import quotidian.Logging
import scala.collection.jcl.Conversions._

object DatastorePersister extends Persister with Logging {
	val datastore = DatastoreServiceFactory.getDatastoreService()
	def save(obj:Savable):Serializable = {
		val entity = if (obj.id == 0) new Entity(obj.table) else new Entity(obj.table,obj.id.toString)
		val map = obj.fields zip obj.values
		map.foreach(t => {
			entity.setProperty(t._1,t._2)
		})
		datastore.put(entity)
	}
	def get(table:String,id:Serializable):Savable = {
		if (id.isInstanceOf[Key]) {
			val mapFcn = PersisterHelper.fetch(table)
			val entity = datastore.get(id.asInstanceOf[Key])
			mapFcn(PersisterHelper.toXml(entity))
		} else {
			throw new IllegalArgumentException("id must be of type com.google.appengine.api.datastore.Key")
		}
	}
	def getAll(table:String):List[Savable] = {
		val query = datastore.prepare(new Query(table))
		val mapFcn = PersisterHelper.fetch(table)
		val entities = query.asList(withLimit(10))
		val it = for {
			val entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		it.toList
	}
	def search(table:String,field:String,value:Any):List[Savable] = {
		val query = datastore.prepare(new Query(table).addFilter(field,Query.FilterOperator.EQUAL,value))
		val mapFcn = PersisterHelper.fetch(table)
		val entities = query.asList(withLimit(10))
		val it = for {
			val entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		it.toList
	}
}