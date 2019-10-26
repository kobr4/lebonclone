package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}


@Singleton
class SearchController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.search())
  }

  def doSearch(search: String) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.search())
  }

}
