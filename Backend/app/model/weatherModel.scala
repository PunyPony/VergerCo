package models
import java.sql.Timestamp
import java.util.Date

import javax.inject.Inject

import scala.concurrent.Future
import play.api.db.DBApi
import anorm.SqlParser.{ get, scalar }
import anorm._


case class Weather(id: Option[Long] = None,
                    sunshine: Option[Boolean],
                    temperature: Option[Float],
                    humidity: Option[Float],
                    wind: Option[Float],
                    timeStamp: Option[Timestamp])
object Weather {
  implicit def toParameters: ToParameterList[Weather] =
    Macro.toParameters[Weather]
}

@javax.inject.Singleton
class WeatherRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

//  // -- Queries
//
//  /**
//    * Retrieve a computer from the id.
//    */
//  def findById(id: Long): Future[Option[Computer]] = Future {
//    db.withConnection { implicit connection =>
//      SQL"select * from computer where id = $id".as(simple.singleOpt)
//    }
//  }(ec)
//
//  /**
//    * Return a page of (Computer,Company).
//    *
//    * @param page Page to display
//    * @param pageSize Number of computers per page
//    * @param orderBy Computer property used for sorting
//    * @param filter Filter applied on the name column
//    */
//  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[Page[(Computer, Option[Company])]] = Future {
//
//    val offset = pageSize * page
//
//    db.withConnection { implicit connection =>
//
//      val computers = SQL"""
//        select * from computer
//        left join company on computer.company_id = company.id
//        where computer.name like ${filter}
//        order by ${orderBy} nulls last
//        limit ${pageSize} offset ${offset}
//      """.as(withCompany.*)
//
//      val totalRows = SQL"""
//        select count(*) from computer
//        left join company on computer.company_id = company.id
//        where computer.name like ${filter}
//      """.as(scalar[Long].single)
//
//      Page(computers, page, offset, totalRows)
//    }
//  }(ec)

//  /**
//    * Update a computer.
//    *
//    * @param id The computer id
//    * @param computer The computer values.
//    */
//  def update(id: Long, computer: Computer) = Future {
//    db.withConnection { implicit connection =>
//      SQL("""
//        update computer set name = {name}, introduced = {introduced},
//          discontinued = {discontinued}, company_id = {companyId}
//        where id = {id}
//      """).bind(computer.copy(id = Some(id)/* ensure */)).executeUpdate()
//      // case class binding using ToParameterList,
//      // note using SQL(..) but not SQL.. interpolation
//    }
//  }(ec)

  /**
    * Insert a new computer.
    *
    * @param computer The computer values.
    */
  def insert(weather: Weather): Future[Option[Long]] = Future {
    println(weather)
    db.withConnection { implicit connection =>
      val p = SQL("""
        insert into WEATHER values (
          (select next value for weather_seq),
          {sunshine}, {temperature}, {humidity}, {wind}, {timeStamp}
        )
      """).bind(weather).executeInsert()
      print(p)
      p
    }
  }(ec)

//
//  /**
//    * Delete a computer.
//    *
//    * @param id Id of the computer to delete.
//    */
//  def delete(id: Long) = Future {
//    db.withConnection { implicit connection =>
//      SQL"delete from computer where id = ${id}".executeUpdate()
//    }
//  }(ec)

}