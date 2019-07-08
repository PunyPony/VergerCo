package sparkreader

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import play.api.libs.json._
import play.api._



object SparkReader {
    def vergerSick()
    {
        val quality = ss.read.parquet("/tmp/data/quality.parquet")
        val state = ss.read.parquet("/tmp/data/state.parquet")

        val join = quality.join(state, "id")
        val northnum = join.filter("sickness == True").filter("lat > 0").select("id").groupBy("id").count.groupBy().sum("count")
        val southnum = join.filter("sickness == True").filter("lat < 0").select("id").groupBy("id").count.groupBy().sum("count")
        val values = Seq(northnum.collect.map(_(0)).toList(0),
            southnum.collect.map(_(0)).toList(0))
        values.map(nb => Option(nb).getOrElse(0))
    }

    def low(): Unit =
    {
        val alert = ss.read.parquet("/tmp/data/alert.parquet")
        val lows = alert.filter("alertType like '%battery%'").count()
        println(low)
    }

    val ss = SparkSession.builder().master("local[*]").appName("Reader").getOrCreate()
    val sc = ss.sparkContext

}


