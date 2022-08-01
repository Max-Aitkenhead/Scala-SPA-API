package controllers

import java.util.UUID

import dbHandlers.ContentColInterface
import javax.inject.{Inject, Singleton}
import models.PageContent
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.filters.csrf.CSRFAddToken

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ContentController @Inject()
(val controllerComponents: ControllerComponents, addToken: CSRFAddToken) extends BaseController with SharedController {

  private def newPageContentFromJson()(implicit request: Request[AnyContent]): Option[PageContent] = {
    for {
      name <- request.body.asJson.map(_ \ "name").flatMap(_.toOption)
      path <- request.body.asJson.map(_ \ "path").flatMap(_.toOption)
      markup <- request.body.asJson.map(_ \ "markup").flatMap(_.toOption)
      css <- request.body.asJson.map(_ \ "css").flatMap(_.toOption)
      javascript <- request.body.asJson.map(_ \ "javascript").flatMap(_.toOption)
      access <- request.body.asJson.map(_ \ "access").flatMap(_.toOption)
    } yield PageContent(UUID.randomUUID().toString, name.as[String], path.as[String], markup.as[String], css.as[String], javascript.as[String], access.as[String])
  }

  def insertNewPage(): Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    val contentDb = new ContentColInterface
    val content = newPageContentFromJson
    val x = newPageContentFromJson getOrElse println("badjson")
    content.map(contentDb.insertNewPage(_)).map(writeresFuture => writeresFuture.flatMap(writeres => Future {
      if(writeres.successful) successResponse("Content set")
      else errorResponse("Content not set")
    })) getOrElse Future(errorResponse("Received invalid JSON"))
  })

  def getPage(): Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    println("thingy")
    val contentDb = new ContentColInterface
    request.body.asJson.map(path => contentDb.getPage(List("path" -> path.as[String])).flatMap(page => Future{
      page.map(p => authResponse(buildJson(p.toJson()))) getOrElse errorResponse("Page does not exist")
    })) getOrElse Future(errorResponse("bad request"))
  })


}
