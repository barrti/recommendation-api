package controllers

import javax.inject.Inject

import dao.UserDAO
import models.User
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(userDAO: UserDAO, controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  implicit val userFormat = Json.format[User]

  def create = Action.async(parse.json) {
    request => {
      val obj = Json.fromJson[User](request.body)
      userDAO.getOrCreate(obj.get.userName, User(Option(System.currentTimeMillis()), obj.get.password, obj.get.userName)).map {
        result => Created(Json.toJson(result))
      }.recoverWith {
        case e => Future {
          InternalServerError("Error: " + e)
        }
      }
    }
  }

  def getOne(id: Long) = Action.async { request =>
    userDAO.getOne(id).map {
      case Some(user) => Ok(Json.toJson(user)).as("application/json")
      case None => NotFound
    }
  }

}
