package dbHandlers

import models.{User, WriteRes}
import reactivemongo.api.MongoConnection
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader}

import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
//import dbHandlers.GenericColInterface
import traits.BSONable

class UserColInterface {

  implicit object UserReader extends BSONDocumentReader[User] {
    def readDocument(doc: BSONDocument): Try[User] = for {
      id <- doc.getAsTry[String]("_id")
      username <- doc.getAsTry[String]("Name")
      password <- doc.getAsTry[String]("Password")
      info <- doc.getAsTry[String]("Info")
      permissions <- doc.getAsTry[String]("Permissions")
    } yield User(id, username, password, info, permissions)
  }

  def getCollectionConnection(connection: MongoConnection): Future[BSONCollection] =
    connection.database("testdb").map(_.collection("testcollection"))

  val driver = new reactivemongo.api.AsyncDriver

  val connection: Future[MongoConnection] = driver.connect(List("localhost"))

  val BSONcollection: Future[BSONCollection] = connection.flatMap(getCollectionConnection)


  def getUsers(queryList: List[(String, String)]): Future[Option[User]] = { // only returns one user
    val gen = new GenericColInterface
    BSONcollection.flatMap(gen.getItems[User](queryList, _))
  }

  def insertUser(user: User): Future[WriteRes] = {
    val gen = new GenericColInterface
    BSONcollection.flatMap(gen.getItems[User](List("Name" -> user.username), _)).flatMap(existingUser =>
      if (existingUser.isEmpty)
        BSONcollection.flatMap(gen.insertItem(user, _)).map(w => WriteRes(w.ok, w.n))
      else Future(WriteRes(successful = false, 0, "User already exists"))
    )
  }

  def updateUser(userId: String, user: BSONable): Future[WriteRes] = {
    val gen = new GenericColInterface
    BSONcollection.flatMap(gen.updateItem(userId, user, _)).map(w => WriteRes(w.ok, w.n))
  }

  def deleteUser(user: User): Future[WriteRes] = {
    val gen = new GenericColInterface
    BSONcollection.flatMap(gen.deleteItem(user.id, _)).map(w => WriteRes(w.ok, w.n))
  }
}
