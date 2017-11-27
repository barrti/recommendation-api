package controllers

import javax.inject.Inject

import dao.MovieDAO
import models.Movie
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._

import scala.concurrent.ExecutionContext

class MovieController @Inject()(movieDAO: MovieDAO, controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  implicit val format = Json.format[Movie]

  def index(size: Int, page: Int, title: Option[String]) = Action.async {
    movieDAO.all(size, page, title).map { case (movies) => Ok(Json.toJson(movies))}
  }

  def findOne(id: Long) = Action.async {
    movieDAO.one(id).map { case (movie) => Ok(Json.toJson(movie))}
  }

}