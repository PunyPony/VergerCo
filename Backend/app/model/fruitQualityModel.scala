package models

import java.sql.Timestamp
import java.util.Date

import javax.inject.Inject

import scala.concurrent.Future
import play.api.db.DBApi
import anorm.SqlParser.{get, scalar}
import anorm._


case class FruitQuality(id: Option[Long] = None,
                        objectID: Option[Int],
                        mature: Option[Boolean],
                        sickness: Option[Boolean],
                        timeStamp: Option[Date] = None)


object FruitQuality {
  implicit def toParameters: ToParameterList[FruitQuality] =
    Macro.toParameters[FruitQuality]
}

@javax.inject.Singleton
class FruitQualityRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

//  /**
//    * Retrieve a fruitQuality from the id.
//    */
//  def findById(id: Long): Future[Option[FruitQuality]] = Future {
//    db.withConnection { implicit connection =>
//      SQL"select * from FRUITQUALITY where id = $id".as(simple.singleOpt)
//    }
//  }(ec)

  val FruitQualityParser: RowParser[FruitQuality] = (
    get[Option[Long]]("id") ~
      get[Option[Int]]("objectID") ~
      get[Option[Boolean]]("mature") ~
      get[Option[Boolean]]("sickness")~
      get[Option[Date]]("timeStamp")
    ) map {
    case id ~ objectID ~ mature ~ sickness ~ timeStamp => // etc...
      FruitQuality(id, objectID, mature, sickness, timeStamp) // etc...
  }

  val allRowsParser: ResultSetParser[List[FruitQuality]] = FruitQualityParser.*

  /**
    * Insert a new fruitQuality.
    *
    * @param fruitQuality The fruitQuality values.
    */
  def insert(fruitQuality: FruitQuality): Future[Option[Long]] = Future {
    db.withConnection { implicit connection =>
      val p = SQL(
        """
        insert into FRUITQUALITY values (
          (select next value for fruitQuality_seq),
          {objectID}, {mature}, {sickness}, CURRENT_TIMESTAMP()
        )""").bind(fruitQuality).executeInsert()
      p
    }
  }(ec)

  def list(pageSize: Int = 10): Future[List[FruitQuality]] = Future {

    //val offest = pageSize * page

    db.withConnection { implicit connection =>

      val fruitsQuality = SQL(
        """
          select * from FRUITQUALITY
          order by timestamp nulls last
          limit {pageSize}
        """
      ).on(
        'pageSize -> pageSize,
      ).as(allRowsParser)
      fruitsQuality

    }

  }(ec)

  /**
    * Delete a fruitQuality.
    *
    * @param id Id of the fruitQuality to delete.
    */
  def delete(id: Long) = Future {
    db.withConnection { implicit connection =>
      SQL"delete from FRUITQUALITY where id = ${id}".executeUpdate()
    }
  }(ec)

}