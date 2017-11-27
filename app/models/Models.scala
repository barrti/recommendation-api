package models

case class Movie(id: Long, title: String)

case class Review(id: Option[Long], comment: Option[String], rate: Double, userId: Long, movieId: Long)

