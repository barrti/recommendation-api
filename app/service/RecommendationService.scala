package service

import javax.inject.Inject

import com.typesafe.config.ConfigFactory
import dao.MovieDAO
import dtos.RecommendationDTO
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class RecommendationService @Inject()(ws: WSClient, movieDAO: MovieDAO)(
    implicit ec: ExecutionContext) {

  private val conf = ConfigFactory.load()

  case class RecommendationResponse(jobId: String, result: Map[String, Double])

  implicit val format = Json.format[RecommendationResponse]

  def getRecommendation(userId: Long, size: Int): Future[Iterable[RecommendationDTO]] = {
    ws.url(conf.getString("recommendation-api.recommendation.service.url"))
      .addHttpHeaders(("ContentType", "application/json"))
      .post(Json.obj("userId" -> userId.toLong, "size" -> size.toInt))
      .map(responseFromRecommendationService => Json.fromJson[RecommendationResponse](responseFromRecommendationService.json))
      .map(recommend => recommend.get.result)
      .map(
        mapOfRecommendations =>
          mapOfRecommendations.keys.map(
            movieId =>
              movieDAO
                .one(movieId.toLong)
                .map(movie =>
                  RecommendationDTO(
                    movie.get.id,
                    movie.get.title,
                    Math.round(mapOfRecommendations(movieId) / 6.0 * 100.0).toInt))))
      .map(Future.sequence(_))
      .flatMap(r => r)
  }

}
