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
                        timeStamp: Option[Timestamp] = None)


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