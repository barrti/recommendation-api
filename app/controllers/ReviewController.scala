package controllers

import javax.inject.Inject

import dao.ReviewDAO
import models.Review
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class ReviewController @Inject()(reviewDAO: ReviewDAO, controllerComponents: ControllerComponents)
                                (implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  implicit val format = Json.format[Review]

  def findReviews(userId: Option[Long], movieId: Option[Long]) =
    Action.async(reviewDAO.all(userId, movieId).map({
      case (review) => Ok(Json.toJson(review))
    }))

  def insert() = Action.async(parse.json) {
    request => {
      val obj = Json.fromJson[Review](request.body)
      reviewDAO.insert(obj.get).map { result =>
        Created(Json.toJson(result))
      }
    }
  }

}
