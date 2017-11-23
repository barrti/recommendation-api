package dao

import javax.inject.Inject

import models.Movie
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class MovieDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Movies = TableQuery[MoviesTable]

  def all(): Future[Seq[Movie]] = db.run(Movies.result)
  def insert(movie: Movie): Future[Unit] = db.run(Movies += movie).map { _ => () }

  private class MoviesTable(tag: Tag) extends Table[Movie](tag, "movie") {

    def id = column[Long]("id", O.PrimaryKey)
    def title = column[String]("title")

    def * = (id, title) <> (Movie.tupled, Movie.unapply)
  }

}
