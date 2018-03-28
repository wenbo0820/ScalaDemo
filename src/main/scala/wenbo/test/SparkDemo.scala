package wenbo.test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Hello world!
  *
  */
object SparkDemo extends App {

  System.setProperty("hadoop.home.dir", "D:\\hadoop-common-2.2.0-bin-master");
  val config = new SparkConf().setMaster("local").setAppName("SparkDemo")
  val sc = new SparkContext(config)
      val textFile = sc.textFile("D://hello.txt")
      val counts = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _).collect().foreach(println)
//  val counts2 = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).groupByKey().collect().foreach(println)

//  val data = sc.parallelize(Array(1, 2, 3, 4, 5, 6))
//  val tt = for (a <- data) yield a * 2   // for()yield 等价于 map
//  var count = tt.reduce((x, y) => x + y)
//  println(count)


}

object MyClass {
  val field = "hello"

  def addHello(rdd: RDD[Int]): RDD[String] = {
    //    val field_ = this.field
    rdd.map(fun)
  }

  def fun(s: Int): String = {
    "hello" + s
  }
}
