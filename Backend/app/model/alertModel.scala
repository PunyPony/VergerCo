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
                 timeStamp: Option[Timestamp] = None)

object Alert {
  implicit def toParameters: ToParameterList[Alert] =
    Macro.toParameters[Alert]
}

@javax.inject.Singleton
class AlertRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

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

}