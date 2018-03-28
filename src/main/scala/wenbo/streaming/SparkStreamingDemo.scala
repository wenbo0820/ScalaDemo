package wenbo.streaming

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by GongWenBo on 2018/1/23.
  */
object SparkStreamingDemo extends App {
  System.setProperty("hadoop.home.dir", "D:\\hadoop-common-2.2.0-bin-master");

  /**
    * 定义context
    */
  val conf = new SparkConf().setMaster("local[2]").setAppName("sparkStreamingDemo")
  val ssc = new StreamingContext(conf, Seconds(10))

  val lines = ssc.socketTextStream("localhost", 9999)


  val words = lines.flatMap(_.split(" "))
  val pairs = words.map(word => (word, 1))
  val counts = pairs.reduceByKey(_ + _)

//  counts.print()
  counts.foreachRDD( a => a.foreach(println))


  // run with spark SQL
  words.foreachRDD{rdd =>
    val sparkSQL = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
    import sparkSQL.implicits._
    val wordsDF = rdd.map(w => Record(w)).toDF()
    wordsDF.createOrReplaceTempView("words")
    sparkSQL.sql("select word ,count(*) as total from words group by word").show()
  }

  ssc.start()
  ssc.awaitTermination()
}


case class Record(word: String)