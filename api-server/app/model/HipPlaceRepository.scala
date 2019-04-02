package model


import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @param dbConfigProvider
  * @param ec
  */
@Singleton
class HipPlaceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  // Brings the Slick DSL into scope, which lets you define the table and other queries.
  import profile.api._

  // The starting point for all queries on the HIP_PLACE table.
  private val HipPlaceAll = TableQuery[HipPlaceListTable]


  /**
    * Define Method
    */
  def all: Future[Seq[HipPlace]] = db.run(HipPlaceAll.result)
  def findByName(name: String): Future[Option[HipPlace]] =
    db.run(HipPlaceAll.filter(_.name === name).result.headOption)

  /**
    * Define Table
    */
  class HipPlaceListTable(tag: Tag) extends Table[HipPlace](tag, "HIP_PLACE") {
    def business_id = column[String]("business_id")

    def name = column[String]("name")

    def address = column[String]("address")

    def city = column[String]("city")

    def latitude = column[String]("latitude")

    def longitude = column[String]("longitude")

    def * = (business_id, name, address, city, latitude, longitude) <> ((HipPlace.apply _).tupled, HipPlace.unapply)
  }
}