package quotidian.persistence.datastore

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,Key,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withLimit}
import basic.persistence.{Persister,Savable}
import java.io.Serializable
import quotidian.Logging
import scala.collection.jcl.Conversions._

object DatastorePersister extends Persister with Logging {
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def save(obj:Savable):Serializable = {
		val entity = if (obj.id == 0) new Entity(obj.table) else new Entity(obj.table,obj.id.toString)
		val properties = obj.fields zip obj.values
		properties.foreach(t => {
			entity.setProperty(t._1,t._2)
		})
// TODO: set created date
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
	def all(table:String):List[Savable] = {
		val query = datastore.prepare(new Query(table))
// TODO: sort by created date
		val mapFcn = PersisterHelper.fetch(table)
		val entities = query.asList(withLimit(10))
		val savables = for {
			val entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		savables.toList
	}
	def count(table:String) = datastore.prepare(new Query(table)).countEntities
	def search(table:String,field:String,value:Any):List[Savable] = {
		val query = datastore.prepare(new Query(table).addFilter(field,Query.FilterOperator.EQUAL,value))
		val mapFcn = PersisterHelper.fetch(table)
		val entities = query.asList(withLimit(10))
		val savables = for {
			val entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		savables.toList
	}
	def some(table:String,o:Int,n:Long):List[Savable] = all(table)
}
