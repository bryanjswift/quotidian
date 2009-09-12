package quotidian.persistence.datastore

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,Key,KeyFactory,Query}
import com.google.appengine.api.datastore.FetchOptions.Builder.{withLimit}
import basic.persistence.{Persister,Savable}
import java.io.Serializable
import java.util.Calendar
import quotidian.Logging
import scala.collection.jcl.Conversions._

object DatastorePersister extends Persister with Logging {
	private val DateCreated = "datecreated"
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def save(obj:Savable):Serializable = {
		val entity = if (obj.id == 0) new Entity(obj.table) else new Entity(obj.table,obj.id.toString)
		val properties = obj.fields zip obj.values
		properties.foreach(t => {
			entity.setProperty(t._1,t._2)
		})
		entity.setProperty(DateCreated,Calendar.getInstance.getTimeInMillis)
		datastore.put(entity)
	}
	def get(table:String,id:Serializable):Savable = {
		val mapFcn = PersisterHelper.fetch(table)
		val key = if (id.isInstanceOf[Key]) {
			id.asInstanceOf[Key]
		} else if (id.isInstanceOf[Long] || id.isInstanceOf[Int]) {
			KeyFactory.createKey(table,id.asInstanceOf[Long])
		} else {
			throw new IllegalArgumentException("id must be of type com.google.appengine.api.datastore.Key")
		}
		val entity = datastore.get(key)
		mapFcn(PersisterHelper.toXml(entity))
	}
	def all(table:String):List[Savable] = {
		val query = datastore.prepare(new Query(table).addSort(DateCreated,Query.SortDirection.DESCENDING))
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
	def some(table:String,count:Int,offset:Int):List[Savable] = all(table)
}
