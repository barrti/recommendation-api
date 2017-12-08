package dao

import javax.inject.Inject

import dtos.MovieDTO
import models.Movie
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{GetResult, JdbcProfile}

import scala.concurrent.{ExecutionContext, Future}

class MovieDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                        (reviewDAO: ReviewDAO)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Movies = TableQuery[MoviesTable]

  implicit val getMovieDTOResult = {
    GetResult(r => Option.apply(MovieDTO(r.nextLong, r.nextString, r.nextDouble)))
  }

  def all(size: Int, page: Int, title: Option[String]): Future[Seq[Movie]] = db.run(Movies
    .filter(_.title.toLowerCase like "%" + title.getOrElse("") + "%")
    .drop(size * page)
    .take(size)
    .result)

  def one(id: Long): Future[Option[Movie]] = db.run(Movies.filter(_.id === id).result.headOption)


  def getMovieByPredicates(id: Long): DBIO[Option[MovieDTO]] =
    sql"""SELECT m.id, m.title, avg(r.rate) FROM movie m JOIN review r ON r.movie_id = id """.as[Option[MovieDTO]].head

  def getMovie(id: Long): Future[Option[MovieDTO]] = db.run(getMovieByPredicates(id))

  private class MoviesTable(tag: Tag) extends Table[Movie](tag, "movie") {

    def id = column[Long]("id", O.PrimaryKey)

    def title = column[String]("title")

    def * = (id, title) <> (Movie.tupled, Movie.unapply)
  }

}
