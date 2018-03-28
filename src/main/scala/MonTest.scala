import org.mongodb.scala.bson.collection.Document
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, Observable}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by GongWenBo on 2018/2/12.
  */
object MonTest {


  def main(args: Array[String]): Unit = {
    import  scala.concurrent.ExecutionContext.Implicits.global
    val client: MongoClient = MongoClient()
    val database: MongoDatabase = client.getDatabase("touty")
    val collection: MongoCollection[Document] = database.getCollection("processes")
    val f :  Observable[String]  =  client.listDatabaseNames()
    f.andThen{
      case Success(s)=>println(s)
      case Failure(t)=>println(t)
    }
    Thread.sleep(3000)



  }

}
