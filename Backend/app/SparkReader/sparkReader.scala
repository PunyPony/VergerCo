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
    def VergerSick()
    {
        val df = ss.read.parquet("/tmp/data/quality.parquet")
        df.filter("sickness == True").show()
    }

    val ss = SparkSession.builder().master("local[*]").appName("Reader").getOrCreate()
    val sc = ss.sparkContext

}


