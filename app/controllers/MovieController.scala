package controllers

import javax.inject.Inject

import dao.MovieDAO
import dtos.MovieDTO
import models.Movie
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class MovieController @Inject()(movieDAO: MovieDAO, controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  implicit val format = Json.format[Movie]
  implicit val dtoFormat= Json.format[MovieDTO]

  def index(size: Int, page: Int, title: Option[String]) = Action.async {
    movieDAO.all(size, page, title).map { case (movies) => Ok(Json.toJson(movies))}
  }

  def findOne(id: Long) = Action.async {
    movieDAO.one(id).map { case (movie) => Ok(Json.toJson(movie))}
  }

  def getMovie(id: Long)= Action.async {
    movieDAO.getMovie(id).map{
      case (movie)=> Ok(Json.toJson(movie))
    }
  }

}