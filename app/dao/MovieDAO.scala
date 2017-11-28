package dao

import javax.inject.Inject

import models.Movie
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class MovieDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                        (reviewDAO: ReviewDAO)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val Movies = TableQuery[MoviesTable]

  def all(size: Int, page: Int, title: Option[String]): Future[Seq[Movie]] = db.run(Movies
    .filter(_.title.toLowerCase like "%" + title.getOrElse("") + "%")
    .drop(size * page)
    .take(size)
    .result)

  def one(id: Long): Future[Option[Movie]] = db.run(Movies.filter(_.id === id).result.headOption)

//  def getMovieById(id: Long, userId: Long): Future[Option[Movie]] = {//TODO
//    val query = for {
//      (m, r) <- (Movies join reviewDAO.Reviews on (_.id === _.movieId)).filter(table => List(
//        Option.apply(id).map(table._2.movieId === _),
//        Option.apply(userId).map(table._2.userId === _)
//      ).collect({
//        case Some(criteria) => criteria
//      }).reduceLeftOption(_ && _).getOrElse(true: Rep[Boolean]))
//    } yield (m.id, m.title, r)
//  }

  private class MoviesTable(tag: Tag) extends Table[Movie](tag, "movie") {

    def id = column[Long]("id", O.PrimaryKey)

    def title = column[String]("title")

    def * = (id, title) <> (Movie.tupled, Movie.unapply)
  }

}
