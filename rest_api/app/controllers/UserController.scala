package controllers

import java.util.UUID
import dbHandlers.UserColInterface
import javax.inject._
import models.{User, UserStub, UserStubWP}
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc._
import play.filters.csrf.CSRFAddToken
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserController @Inject()
(val controllerComponents: ControllerComponents, addToken: CSRFAddToken) extends BaseController with SharedController {

  private def userStubFromJson()(implicit request: Request[AnyContent]): Option[UserStub] = {
    val username = request.body.asJson.map(_ \ "user" \ "username").flatMap(_.toOption)
    val info = request.body.asJson.map(_ \ "user" \ "info").flatMap(_.toOption)
    username.flatMap(u => info.map(i => UserStub(u.as[String], i.as[String])))
  }

  private def newUserFromJson()(implicit request: Request[AnyContent]): Option[User] = {
    val username = request.body.asJson.map(_ \ "user" \ "username").flatMap(_.toOption)
    val password = request.body.asJson.map(_ \ "user" \ "password").flatMap(_.toOption)
    val info = request.body.asJson.map(_ \ "user" \ "info").flatMap(_.toOption)
    username.flatMap(u => password.flatMap(p => info.map(i =>
      User(UUID.randomUUID().toString, u.as[String], BCrypt.hashpw(p.as[String], BCrypt.gensalt), i.as[String], "perms"))))
  }

  private def userStubWPFromJson()(implicit request: Request[AnyContent]): Option[UserStubWP] = {
    val username = request.body.asJson.map(_ \ "username").flatMap(_.toOption)
    val password = request.body.asJson.map(_ \ "password").flatMap(_.toOption)
    username.flatMap(u => password.map(p => UserStubWP(u.as[String], p.as[String])))
  }

  def getSession: Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    println("yaaaaaaay")
    val userDb = new UserColInterface
    val userId = request.session.get("sessionToken").map(_.split("_").head)
    val userFuture = userId.map(u => userDb.getUsers(List("_id" -> u)))
    userFuture.map(_.flatMap(user => Future {
      user.map(u => authResponse(buildJson(List(u.toStub().toJson())))) getOrElse errorResponse("Invalid Session")
    })) getOrElse Future(errorResponse("Invalid Session"))
  })

  def login: Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    val userDb = new UserColInterface
    val userDetails = userStubWPFromJson()
    userDetails.map(userstubWP => userDb.getUsers(List("Name" -> userstubWP.username))).map(_.flatMap(user => Future {
      user.map(u => {
        if (BCrypt.checkpw(userDetails.get.password, u.password)) newAuthResponse(buildJson(u.toStub().toJson()), u.id)
        else errorResponse("Incorrect password")
      }) getOrElse errorResponse("User does not exist")
    })) getOrElse Future(errorResponse("Bad request"))
  })

  def logUserOut: Action[AnyContent] = addToken(Action { implicit request: Request[AnyContent] =>
    Ok(buildJson(""""log_out": "successful"""")).withNewSession
  })

  //TODO currently only gets one user - change to many users
  def getUsers: Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    val query = request.body.asFormUrlEncoded // TODO needs to be switched to json!!
      .map(req => req.filter(x => x._1.startsWith("query"))
        .map(x => (x._1.substring(6).stripSuffix("]"), x._2.head)).toList)
    val userDb = new UserColInterface
    query.map(userDb.getUsers(_).flatMap(user => Future {
      user.map(u => authResponse(buildJson(u.toStub().toJson()))) getOrElse errorResponse("Unwritten error")
    })) getOrElse Future(errorResponse("Received invalid JSON"))
  })

  def insertNewUser(): Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    val userDb = new UserColInterface
    val user = newUserFromJson
    user.map(u => userDb.insertUser(u).flatMap(writeres => Future {
      if (writeres.successful) newAuthResponse(buildJson(List(writeres.toJson(), u.toStub().toJson())), u.id)
      else errorResponse(writeres.errorMsg)
    })) getOrElse Future(errorResponse("Received invalid JSON"))
  })

  def updateUser(): Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    val newUserStubObj = userStubFromJson() // need to decide between fully updating or updating from user stub
    val userId = request.session.get("sessionToken").map(_.split("_").head)
    val userDb = new UserColInterface
    userId.flatMap(id => newUserStubObj.map(user => userDb.updateUser(id, user).flatMap(writeRes => Future {
      authResponse(buildJson(List(writeRes.toJson(), user.toJson())))
    }))) getOrElse Future(errorResponse(""))
  })

}
