package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

@Singleton
class LoginController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

  def doLogin(email: String, password: String) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

}
