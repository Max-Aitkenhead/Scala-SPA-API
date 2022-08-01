package controllers

import javax.inject.Singleton
import models.SessionDAO
import play.api.mvc.{AnyContent, Request, Result}
import play.filters.csrf.CSRF
import play.filters.csrf.CSRF.Token
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

@Singleton
trait SharedController extends BaseController {

  def authResponse(json: JsValue)(implicit request: Request[AnyContent]): Result =
    if(sessionIsEmpty) noSessionResponse()
    else Ok(json).withSession(request.session + ("sessionToken" -> request.session.get("sessionToken").get))

  def newAuthResponse(json: JsValue, id: String)(implicit request: Request[AnyContent]): Result =
    Ok(json).withSession(request.session + ("sessionToken" -> SessionDAO.generateToken(id)))

  def errorResponse(msg: String)(implicit request: Request[AnyContent]): Result =
    if(sessionIsEmpty) noSessionResponse(msg)
    else Ok(buildJson(s""""error": "$msg""""))
      .withSession(request.session + ("sessionToken" -> request.session.get("sessionToken").get))

  def noSessionResponse(msg: String = "No Valid Session")(implicit request: Request[AnyContent]): Result =
    Ok(buildJson(s""""error": "$msg"""")).withNewSession

  def successResponse(msg: String = "Success")(implicit request: Request[AnyContent]): Result =
    Ok(buildJson(s""""success":"$msg""""))
      .withSession(request.session + ("sessionToken" -> request.session.get("sessionToken").get))

  private def sessionIsEmpty()(implicit request: Request[AnyContent]): Boolean =
    request.session.get("sessionToken").isEmpty

  def buildJson(inputs: List[String])(implicit request: Request[AnyContent]): JsValue = {
    val Token(_, newToken) = CSRF.getToken(request).get
    @scala.annotation.tailrec
    def combineJson(jsons: List[String], acc: String): String = {
      if(jsons.isEmpty) acc
      else combineJson(jsons.tail, acc + jsons.head + ", ")
    }
    Json.parse(s"""{${combineJson(inputs, "")} "token" : "$newToken"}""")
  }

  def buildJson(input: String)(implicit request: Request[AnyContent]): JsValue = {
    val Token(_, newToken) = CSRF.getToken(request).get
    Json.parse(s"""{$input, "token" : "$newToken"}""")
  }
}
