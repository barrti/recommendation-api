package dtos

case class MovieDTO( id: Long, title: String, averageRate: Double )
case class RecommendationDTO(movieId: Long, title: String,  recommendation: Int)





