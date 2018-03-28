package mongo

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}


/**
  * Created by GongWenBo on 2018/2/12.
  */
//class MongoRepository {
//  val mongoClient: MongoClient = MongoClient()
//
//  def collection(db:String, collection:String): MongoCollection[Document] ={
//    val database: MongoDatabase = mongoClient.getDatabase(db)
//    database.getCollection(collection)
//  }
//}
//
//object MongoRepository {
//  def apply(): MongoRepository = new MongoRepository()
//}

object MongoTest  {
  def main(args: Array[String]): Unit = {
    import org.mongodb.scala._

    val mongoClient: MongoClient = MongoClient()

    mongoClient.listDatabaseNames().foreach(println)
  }

}
