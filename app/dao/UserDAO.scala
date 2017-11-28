package dao

import javax.inject.Inject

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {


  import profile.api._

  private val Users = TableQuery[UsersTable]

  def all(): Future[Seq[User]] = db.run(Users.result)

  def getOne(id: Long): Future[Option[User]] = db.run(Users.filter(_.id === id).result.headOption)

  def getUserDBIOById(id: Option[Long]): DBIO[User] = Users.filter(_.id === id).result.head

  def getUserDBIOByName(name: Option[String]): DBIO[User] = Users.filter(_.userName.toLowerCase === name.get.toLowerCase).result.head

  def getOrCreate(name: Option[String], user: User): Future[User] = {
    val query = Users.filter(_.userName.toLowerCase === name.get.toLowerCase)
    val existsAction = query.exists.result
    val insertOrUpdateAction = (for {
      exists <- existsAction
      result <- exists match {
        case true => getUserDBIOByName(user.userName).transactionally
        case false => {
          val insertAction = Users returning Users.map(_.id) += user
          val finalAction = insertAction.flatMap(id => getUserDBIOById(id)).transactionally
          finalAction
        }
      }
    } yield result).transactionally
    db.run(insertOrUpdateAction)
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def id = column[Option[Long]]("id", O.PrimaryKey)

    def userName = column[Option[String]]("user_name")

    def password = column[Option[String]]("password")

    def * = (id, userName, password) <> (User.tupled, User.unapply)
  }


}
