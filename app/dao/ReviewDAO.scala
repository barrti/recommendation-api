package dao

import javax.inject.Inject

import models.Review
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ReviewDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                         (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Reviews = TableQuery[ReviewsTable]

  def all(userId: Option[Long], movieId: Option[Long]): Future[Seq[Review]] = db
    .run(Reviews
      .filter(review => List(
        movieId.map(review.movieId === _),
        userId.map(review.userId === _)
      ).collect({
        case Some(criteria) => criteria
      }).reduceLeftOption(_ && _).getOrElse(true: Rep[Boolean]))
      .result)

  def insert(review: Review): Future[Review] = db.run(Reviews returning Reviews += review)


  private class ReviewsTable(tag: Tag) extends Table[Review](tag, "review") {

    def * = (id, comment, rate, userId, movieId) <> (Review.tupled, Review.unapply)

    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def comment = column[Option[String]]("comment")

    def rate = column[Double]("rate")

    def userId = column[Long]("user_id")

    def movieId = column[Long]("movie_id")

  }

}
