package wenbo.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.MutableAggregationBuffer

/**
  * Created by GongWenBo on 2018/1/18.
  */
object SparkSQL extends App {
  val spark = SparkSession.builder()
    .appName("sparkSql").config("spark.some.config.option", "some-value").master("local")
    .getOrCreate()

  import spark.implicits._

  //  val df = spark.read.json("D:\\spark-2.2.1-bin-hadoop2.7\\examples\\src\\main\\resources\\people.json")

  //1.数据查询
  //    df.show()
  //  df.printSchema()
  //  df.select("name").show()
  //  df.select($"name",$"age"+1).show()
  //  df.filter($"age" > 21).show()
  //  df.groupBy("age").count().show()


  //2.将数据转成table  (临时,仅对当前session有效)
  //  df.createOrReplaceTempView("people")
  //  spark.sql("select name from people").show()

  //3.全局
  //  df.createGlobalTempView("people")
  //  spark.sql("select * from global_temp.people").show()

  //4.
  case class Person(name: String, age: Long)

  //  val caseDS = Seq(Person("wenbo",18)).toDS()
  //  caseDS.show()

  //  val primitiveDS = Seq(1,2,3,4,5).toDS()
  //  primitiveDS.map(_*2).collect().foreach(println)   //return Array(2,4,6,8,10)

  //  df.as[Person].show()


  //5. RDDs  to Schema
  //  val peopleTxt = spark.sparkContext.textFile("D:\\spark-2.2.1-bin-hadoop2.7\\examples\\src\\main\\resources\\people.txt")
  //    .map(_.split(",")).map(p => Person(p(0), p(1).trim.toInt)).toDS()
  //  peopleTxt.createOrReplaceTempView("peopleTxt")
  //  val sql = spark.sql("select * from peopleTxt")
  //
  //  sql.map(p =>"Name: "+ p(0)).show()
  //  sql.map(p =>"Age: " + p.getAs[Int]("age")).show()
  //
  //  implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String,Any]]
  //
  //  sql.map(p => p.getValuesMap[Any](List("name","age"))).collect().foreach(println)

  //6. parquet格式
  //  val parquet = spark.read.load("D:\\spark-2.2.1-bin-hadoop2.7\\examples\\src\\main\\resources\\users.parquet")
  //  parquet.select("name","favorite_color").show()

  //7. 直接读取文件
//    val sqlDF = spark.sql("SELECT * FROM parquet.`D:/spark-2.2.1-bin-hadoop2.7/examples/src/main/resources/users.parquet`")

    val sqlDF = spark.sql("SELECT * FROM json.`D:/spark-2.2.1-bin-hadoop2.7/examples/src/main/resources/people.json`")
    sqlDF.select("name","age").write.format("parquet").save("D:\\sql.parquet")

}

