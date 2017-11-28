package models

case class Movie(id: Long, title: String)

case class Review()

case class User(id: Option[Long], password: Option[String], userName: Option[String])