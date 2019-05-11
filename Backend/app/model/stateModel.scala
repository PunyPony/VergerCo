package models

import java.sql.Timestamp
import java.util.Date

import javax.inject.Inject

import scala.concurrent.Future
import play.api.db.DBApi
import anorm.SqlParser.{get, scalar}
import anorm._


case class State(id: Option[Long] = None,
                 objectID: Option[Int],
                 chargeperc: Option[Float],
                 temperature: Option[Float],
                 placename: Option[String],
                 lat: Option[Float],
                 long: Option[Float],
                 timeStamp: Option[Date] = None)

object State {
  implicit def toParameters: ToParameterList[State] =
    Macro.toParameters[State]
}

@javax.inject.Singleton
class StateRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

//
//  /**
//    * Retrieve a state from the id.
//    */
//  def findById(id: Long): Future[Option[State]] = Future {
//    db.withConnection { implicit connection =>
//      SQL"select * from STATE where id = $id".as(simple.singleOpt)
//    }
//  }(ec)

  val StateParser: RowParser[State] = (
    get[Option[Long]]("id") ~
      get[Option[Int]]("objectID") ~
      get[Option[Float]]("chargeperc") ~
      get[Option[Float]]("temperature") ~
      get[Option[String]]("placename") ~
      get[Option[Float]]("lat") ~
      get[Option[Float]]("long") ~
      get[Option[Date]]("timeStamp")
    ) map {
    case id ~ objectID ~ chargeperc ~ temperature ~ placename ~ lat ~ long ~ timeStamp => // etc...
    State(id, objectID, chargeperc, temperature, placename, lat, long, timeStamp) // etc...
  }

  val allRowsParser: ResultSetParser[List[State]] = StateParser.*

  /**
    * Insert a new state.
    *
    * @param state The state values.
    */
  def insert(state: State): Future[Option[Long]] = Future {
    db.withConnection { implicit connection =>
      val p = SQL(
        """
        insert into STATE values (
          (select next value for state_seq),
          {objectID}, {chargeperc}, {temperature}, {placename}, {lat}, {long}, CURRENT_TIMESTAMP()
        )""").bind(state).executeInsert()
      p
    }
  }(ec)

  /**
    * Delete a state.
    *
    * @param id Id of the state to delete.
    */
  def delete(id: Long) = Future {
    db.withConnection { implicit connection =>
      SQL"delete from STATE where id = ${id}".executeUpdate()
    }
  }(ec)

  def list(pageSize: Int = 10): Future[List[State]] = Future {

    //val offest = pageSize * page

    db.withConnection { implicit connection =>

      val states = SQL(
        """
          select * from STATE
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