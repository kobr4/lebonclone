package services

import java.time.ZonedDateTime

import db.UsersRepository
import model.User
import com.github.t3hnar.bcrypt._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}

object UserService extends StrictLogging {

  private val usersRepository = new UsersRepository("mysql")

  def signUp(email: String, password: String)(implicit ec: ExecutionContext): Future[Option[Int]] = {
    usersRepository.selectUserByEmail(email).flatMap {
      case Some(_) =>
        logger.info("User {} already exists in DB", email)
        Future.successful(None)
      case _ =>
        logger.info("Will insert new user {} in DB", email)
        usersRepository.insertUser(User(0, email, password.bcrypt, None, ZonedDateTime.now())).map { i =>
          logger.info("Sending mail to user {}", email)
          //MailService.sendActivationMail(email)
          Some(i)
        }
    }
  }

  def activate(email: String)(implicit ec: ExecutionContext): Future[Int] = {
    usersRepository.selectUserByEmail(email).flatMap {
      case Some(user) =>
        val activatedUser = User(user.id, user.email, user.password, Some(ZonedDateTime.now()), user.created)
        usersRepository.updateUser(activatedUser)
      case _ => Future.successful(0)
    }
  }

  def get(email: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    usersRepository.selectUserByEmail(email)
  }

  def get(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    usersRepository.selectUserById(id)
  }

  def verify(email: String, password: String)(implicit ec: ExecutionContext): Future[Option[Int]] = {
    get(email).map {
      case Some(s) => password.isBcryptedSafe(s.password).toOption.flatMap {
        case true => Some(s.id)
        case _ => None
      }
      case _ => None
    }
  }
}
