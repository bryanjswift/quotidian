package quotidian.persistence.datastore

import com.google.appengine.api.datastore.{DatastoreService,DatastoreServiceFactory,Entity,Key,KeyFactory,Query}
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.FetchOptions.Builder.{withLimit,withOffset}
import basic.persistence.{Persister,Savable}
import java.io.Serializable
import java.util.Calendar
import quotidian.{Logging,UnsupportedMethodException}
import scala.collection.JavaConversions

object DatastorePersister extends Persister with Logging {
	private val DateCreated = "datecreated"
	private val datastore = DatastoreServiceFactory.getDatastoreService()
	def all(table:String):List[Savable] = {
		val query = datastore.prepare(new Query(table).addSort(DateCreated,Query.SortDirection.DESCENDING))
		val mapFcn = PersisterHelper.fetch(table)
		val entities = JavaConversions.asIterable(query.asList(withLimit(Integer.MAX_VALUE)))
		val savables = for {
			entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		savables.toList
	}
	def count(table:String):Int = datastore.prepare(new Query(table)).countEntities
	def delete(table:String,id:Serializable):Unit = datastore.delete(key(table,id))
	def get(table:String,id:Serializable):Savable = {
		val mapFcn = PersisterHelper.fetch(table)
		val entity = datastore.get(key(table,id))
		mapFcn(PersisterHelper.toXml(entity))
	}
	private def key(table:String,id:Serializable):Key = {
		if (id.isInstanceOf[Key]) {
			id.asInstanceOf[Key]
		} else if (id.isInstanceOf[Long] || id.isInstanceOf[Int]) {
			KeyFactory.createKey(table,id.asInstanceOf[Long])
		} else if (id.isInstanceOf[String]) {
			KeyFactory.stringToKey(id.asInstanceOf[String])
		} else {
			throw new IllegalArgumentException("id must be of type com.google.appengine.api.datastore.Key")
		}
	}
	def save(obj:Savable):Serializable = {
		val entity = if (obj.id == 0) new Entity(obj.table) else new Entity(obj.table,obj.id.toString)
		val properties = obj.fields zip obj.values
		properties.foreach(t => {
			entity.setProperty(t._1,t._2)
		})
		entity.setProperty(DateCreated,Calendar.getInstance.getTimeInMillis)
		datastore.put(entity)
	}
	def search(table:String,field:String,value:Any):List[Savable] =
		throw new UnsupportedMethodException("search not implemented for the Datastore")
	def some(table:String,count:Int,offset:Int):List[Savable] = {
		val query = datastore.prepare(new Query(table).addSort(DateCreated,Query.SortDirection.DESCENDING))
		val mapFcn = PersisterHelper.fetch(table)
		val entities = JavaConversions.asIterable(query.asList(withOffset(offset).limit(count)))
		val savables = for {
			entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		savables.toList
	}
	def some(table:String,count:Int,before:Long):List[Savable] = {
		val query = datastore.prepare(new Query(table).addSort(DateCreated,Query.SortDirection.DESCENDING)
				.addFilter(DateCreated,FilterOperator.LESS_THAN,before)
			)
		val mapFcn = PersisterHelper.fetch(table)
		val entities = JavaConversions.asIterable(query.asList(withLimit(count)))
		val savables = for {
			entity <- entities
		} yield mapFcn(PersisterHelper.toXml(entity))
		savables.toList
	}
}
