package controllers

import javax.inject.Inject

import dao.MovieDAO
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class MovieController @Inject()(movieDAO: MovieDAO, controllerComponents: ControllerComponents)(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  def index = Action.async {
    movieDAO.all().map { case (movies) => Ok(views.html.index(movies))}
  }

}