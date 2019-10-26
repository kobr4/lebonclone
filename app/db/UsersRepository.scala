package db

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

import model.User
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class UsersRepository(dbConfigPath: String) {

  val db = Database.forConfig(dbConfigPath)

  def insertUser(user: User): Future[Int] = {
    db.run((UsersRepository.users returning UsersRepository.users.map(_.id)) += user)
  }

  def selectUserByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(UsersRepository.users.filter(_.email === email).result).map(_.headOption)
  }

  def selectUserById(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(UsersRepository.users.filter(_.id === id).result).map(_.headOption)
  }

  def updateUser(user: User): Future[Int] = {
    db.run((for {u <- UsersRepository.users if u.email === user.email} yield u).update(user))
  }

  def create()(implicit ec: ExecutionContext): Future[Unit] = {
    db.run(UsersRepository.users.schema.createIfNotExists)
  }
}

object UsersRepository {

  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC))

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email")

    def password = column[String]("password")

    def activation = column[Option[ZonedDateTime]]("activation")

    def created = column[ZonedDateTime]("created")

    def * = (id, email, password, activation, created) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]

  def createTable()(implicit ec: ExecutionContext): Future[Unit]
    = new UsersRepository("mysql").create()

}

object CreateUserTable extends App {

  implicit val ctxt = ExecutionContext.global

  val f = UsersRepository.createTable()

  f.onComplete {
    case Failure(ex) =>
      println(ex)
    case Success(_) =>
      println("Table USER created")
  }

  Await.ready(f, Duration.Inf)
}