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
class ReviewRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  // Brings the Slick DSL into scope, which lets you define the table and other queries.
  import profile.api._

  // The starting point for all queries on the REVIEWS table.
  private val ReviewAll = TableQuery[ReviewListTable]


  /**
    * Define Method
    */
  def all: Future[Seq[Review]] = db.run(ReviewAll.result)
  def findReviews(business_id: String): Future[Seq[Review]] =
    db.run(ReviewAll.filter(_.business_id === business_id).result)


  /**
    * Define Table
    */
  class ReviewListTable(tag: Tag) extends Table[Review](tag, "REVIEWS") {
    def business_id = column[String]("business_id")

    def date = column[Timestamp]("date")

    def review = column[String]("review")

    def * = (business_id, date, review) <> ((Review.apply _).tupled, Review.unapply)
  }

}