package model

import java.sql.Timestamp

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
class TipRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  // Brings the Slick DSL into scope, which lets you define the table and other queries.
  import profile.api._

  // The starting point for all queries on the TIPS table.
  private val TipAll = TableQuery[TipListTable]


  /**
    * Define Method
    */
  def all: Future[Seq[Tip]] = db.run(TipAll.result)
  def findTips(business_id: String): Future[Seq[Tip]] =
    db.run(TipAll.filter(_.business_id === business_id).result)

  /**
    * Define Table
    */
  class TipListTable(tag: Tag) extends Table[Tip](tag, "TIPS") {
    def business_id = column[String]("business_id")

    def date = column[Timestamp]("date")

    def tip = column[String]("tip")

    def * = (business_id, date, tip) <> ((Tip.apply _).tupled, Tip.unapply)
  }

}