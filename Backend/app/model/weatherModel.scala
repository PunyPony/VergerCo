package models
import java.sql.Timestamp
import java.util.Date

import javax.inject.Inject

import scala.concurrent.Future
import play.api.db.DBApi
import anorm.SqlParser.{ get, scalar }
import anorm._


case class Weather(id: Option[Long] = None,
                   objectID: Option[Int],
                    sunshine: Option[Boolean],
                    temperature: Option[Float],
                    humidity: Option[Float],
                    wind: Option[Float],
                    timeStamp: Option[Date] = None)
object Weather {
  implicit def toParameters: ToParameterList[Weather] =
    Macro.toParameters[Weather]
}

@javax.inject.Singleton
class WeatherRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

//  /**
//    * Retrieve a weather from the id.
//    */
//  def findById(id: Long): Future[Option[Weather]] = Future {
//    db.withConnection { implicit connection =>
//      SQL"select * from WEATHER where id = $id".as(simple.singleOpt)
//    }
//  }(ec)

  val WeatherParser: RowParser[Weather] = (
    get[Option[Long]]("id") ~
      get[Option[Int]]("objectID") ~
      get[Option[Boolean]]("sunshine") ~
      get[Option[Float]]("temperature") ~
      get[Option[Float]]("humidity") ~
      get[Option[Float]]("wind") ~
      get[Option[Date]]("timeStamp")
    ) map {
    case id ~ objectID ~ sunshine ~ temperature ~ humidity ~ wind ~ timeStamp => // etc...
    Weather(id, objectID, sunshine, temperature, humidity, wind, timeStamp) // etc...
  }
  val allRowsParser: ResultSetParser[List[Weather]] = WeatherParser.*

  /**
    * Insert a new weather.
    *
    * @param weather The weather values.
    */
  def insert(weather: Weather): Future[Option[Long]] = Future {
    db.withConnection { implicit connection =>
      val p = SQL("""
        insert into WEATHER values (
          (select next value for weather_seq),
          {objectID}, {sunshine}, {temperature}, {humidity}, {wind}, CURRENT_TIMESTAMP()
        )""").bind(weather).executeInsert()
      p
    }
  }(ec)

  /**
    * Delete a weather.
    *
    * @param id Id of the weather to delete.
    */
  def delete(id: Long) = Future {
    db.withConnection { implicit connection =>
      SQL"delete from WEATHER where id = ${id}".executeUpdate()
    }
  }(ec)


  def list(pageSize: Int = 10): Future[List[Weather]] = Future {

    //val offest = pageSize * page

    db.withConnection { implicit connection =>

      val states = SQL(
        """
          select * from WEATHER
          order by timestamp nulls last
          limit {pageSize}
        """
      ).on(
        'pageSize -> pageSize,
      ).as(allRowsParser)
      states

    }

  }(ec)

}