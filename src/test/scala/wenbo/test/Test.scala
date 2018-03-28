package wenbo.test
import java.util.concurrent.ConcurrentHashMap

import akka.stream.scaladsl.Source
import org.spark_project.jetty.util.ConcurrentHashSet
import spray.json._
import spray.json.DefaultJsonProtocol

import scala.collection.mutable
/**
  * Created by GongWenBo on 2018/2/7.
  */
object Test extends DefaultJsonProtocol{


  def main(args: Array[String]): Unit = {

    val m = Map("innerWenbo" -> "wenbo")
    val map = Map("wenbo" -> m)
    map.get("wenbo") match {
      case Some(m) if m.get("innerWenbo").contains("wenbo1") =>
        println("yes")
      case _=>
        println("No")
    }

    val list = List("wenbo","sss")
    println(list.toJson.compactPrint)

    val s = "[\"wenbo\",\"alan\"]"
    val sl = s.parseJson.convertTo[List[String]]
    println(sl)

    val cmap = new ConcurrentHashMap[String,Boolean]
    if(cmap.putIfAbsent("test",true) == null){
      println("1111")
    }
    println(cmap.putIfAbsent("test",false))
    println(cmap.putIfAbsent("test",false))
    println(cmap.putIfAbsent("test",false))




    val a = (for (i <- 0 to 10 if i!=5)yield (i,i*3)).toMap
    println(a)
  }


}

abstract class A{
  def show(a:Int):Int
}
class B extends A{
  override def show(a: Int): Nothing = ???
}

