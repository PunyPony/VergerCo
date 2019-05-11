package models

import java.sql.Timestamp
import java.util.Date

import javax.inject.Inject

import scala.concurrent.Future
import play.api.db.DBApi
import anorm.SqlParser.{get, scalar}
import anorm._


case class Alert(id: Option[Long] = None,
                 objectID: Option[Int],
                 alertType: Option[String],
                 timeStamp: Option[Date] = None)

object Alert {
  implicit def toParameters: ToParameterList[Alert] =
    Macro.toParameters[Alert]
}

@javax.inject.Singleton
class AlertRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  val AlertParser: RowParser[Alert] = (
  get[Option[Long]]("id") ~
  get[Option[Int]]("objectID") ~
  get[Option[String]]("alertType") ~
  get[Option[Date]]("timeStamp")
  ) map {
    case id ~ objectID ~ alertType ~ timeStamp => // etc...
      Alert(id, objectID, alertType, timeStamp) // etc...
  }

  val allRowsParser: ResultSetParser[List[Alert]] = AlertParser.*

//  /**
//    * Retrieve a alert from the id.
//    */
//  def findById(id: Long): Future[Option[Alert]] = Future {
//    db.withConnection { implicit connection =>
//      SQL"select * from ALERT where id = $id".as(simple.singleOpt)
//    }
//  }(ec)

  /**
    * Insert a new alert.
    *
    * @param alert The alert values.
    */
  def insert(alert: Alert): Future[Option[Long]] = Future {
    db.withConnection { implicit connection =>
      val p = SQL(
        """
        insert into ALERT values (
          (select next value for alert_seq),
          {objectID}, {alertType} ,CURRENT_TIMESTAMP()
        )""").bind(alert).executeInsert()
      p
    }
  }(ec)

  /**
    * Delete a alert.
    *
    * @param id Id of the alert to delete.
    */
  def delete(id: Long) = Future {
    db.withConnection { implicit connection =>
      SQL"delete from ALERT where id = ${id}".executeUpdate()
    }
  }(ec)

  /**
   * Return a page of (Computer,Company).
   *
   * @param page Page to display
   * @param pageSize Number of computers per page
   * @param orderBy Computer property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(/*page: Int = 0, */pageSize: Int = 10/*, orderBy: Int = 1, filter: String = "%"*/): Future[List[Alert]] = Future {

    //val offest = pageSize * page

    db.withConnection { implicit connection =>

      val alerts = SQL(
        """
          select * from alert
          order by timestamp nulls last
          limit {pageSize}
        """
      ).on(
        'pageSize -> pageSize,
      ).as(allRowsParser)
      alerts

    }

  }(ec)

}
