package controllers

import javax.inject._
import model._
import play.api.data.Form
import play.api.data.Forms.{mapping, sqlTimestamp, text}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class BusinessController @Inject()(hipPlaceRepo: HipPlaceRepository, reviewRepo: ReviewRepository, tipRepo: TipRepository,
                                   cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {


  val hipPlaceForm = Form(
    mapping(
      "business_id" -> text(),
      "name" -> text(),
      "address" -> text(),
      "city" -> text(),
      "latitude" -> text(),
      "longitude" -> text()
    )(HipPlace.apply)(HipPlace.unapply)
  )

  val reviewForm = Form(
    mapping(
      "business_id" -> text(),
      "date" -> sqlTimestamp("yyyy-mm-dd hh:mm:ss"),
      "review" -> text()
    )(Review.apply)(Review.unapply)
  )

  val tipForm = Form(
    mapping(
      "business_id" -> text(),
      "date" -> sqlTimestamp("yyyy-mm-dd hh:mm:ss"),
      "tip" -> text()
    )(Tip.apply)(Tip.unapply)
  )


  def live = Action { implicit request => Ok("OK!") }

  /**
    * Hip Place 전체 리스트 출력
    * @return
    */
  def getHipPlaceList = Action.async { implicit request =>
    hipPlaceRepo.all.map { hipPlaces =>
      Ok(Json.toJson(hipPlaces))
    }
  }

  /**
    * Review 전체 리스트 출력
    * @return
    */
  def getReviewList = Action.async { implicit request =>
    reviewRepo.all.map { reviews =>
      Ok(Json.toJson(reviews))
    }
  }

  /**
    * Tip 전체 리스트 출력
    * @return
    */
  def getTipList = Action.async { implicit request =>
    tipRepo.all.map { tips: Seq[Tip] =>
      Ok(Json.toJson(tips))
    }
  }

  /**
    * 가게 이름으로 검색해서 가게 정보와 전체 리뷰와 팁 정보를 출력
    * @param name
    * @return
    */
  def getHipPlaceWithReviewsAndTips(name: String) = Action.async { implicit request =>
    hipPlaceRepo.findByName(name).flatMap {
      case Some(h) =>
        for {
          reviews <- reviewRepo.findReviews(h.business_id)
          tips <- tipRepo.findTips(h.business_id)
        } yield {
          Ok(Json.obj(
            "info" -> h,
            "reviews" -> reviews.map { r =>
              Json.obj(
                "review" -> r.review,
                "date" -> r.date
              )
            },
            "tips" -> tips.map { t =>
              Json.obj(
                "tip" -> t.tip,
                "date" -> t.date
              )
            }
          ))
        }
      case None => Future.apply(Ok(Json.obj("error" -> s"Did not found information of $name.")))
    }
  }
}
