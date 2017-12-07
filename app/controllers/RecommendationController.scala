package controllers

import javax.inject.Inject

import dtos.RecommendationDTO
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import service.RecommendationService

import scala.concurrent.ExecutionContext

class RecommendationController @Inject()(
    recommendationService: RecommendationService,
    ws: WSClient,
    controllerComponents: ControllerComponents)(
    implicit executionContext: ExecutionContext)
    extends AbstractController(controllerComponents) {

  implicit val recommendationDTOFormat = Json.format[RecommendationDTO]

  def getRecommendations(userId: Long, size: Int): Action[AnyContent] = {
    Action.async {
      recommendationService.getRecommendation(userId, size).map { response =>
        Ok(Json.toJson(response))
      }
    }
  }

}
