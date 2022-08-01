package dbHandlers

import models.{PageContent, WriteRes}
import reactivemongo.api.MongoConnection
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class ContentColInterface {

  def getCollectionConnection(connection: MongoConnection): Future[BSONCollection] =
    connection.database("testdb").map(_.collection("testContent"))

  val driver = new reactivemongo.api.AsyncDriver

  val connection: Future[MongoConnection] = driver.connect(List("localhost"))

  val BSONcollection: Future[BSONCollection] = connection.flatMap(x => getCollectionConnection(x))

  implicit object UserReader extends BSONDocumentReader[PageContent] {
    def readDocument(doc: BSONDocument): Try[PageContent] = for {
      id <- doc.getAsTry[String]("_id")
      name <- doc.getAsTry[String]("name")
      path <- doc.getAsTry[String]("path")
      markup <- doc.getAsTry[String]("markup")
      css <- doc.getAsTry[String]("css")
      js <- doc.getAsTry[String]("javascript")
      access <- doc.getAsTry[String]("access")

    } yield PageContent(id, name, path, markup, css, js, access)
  }

  def insertNewPage(content: PageContent): Future[WriteRes] = {
    val gen = new GenericColInterface
    BSONcollection.flatMap(gen.insertItem(content, _)).map(w => WriteRes(w.ok, w.n))
  }

  def getPage(queryList: List[(String, String)]): Future[Option[PageContent]] = { // only returns one page
    val gen = new GenericColInterface
    BSONcollection.flatMap(gen.getItems[PageContent](queryList, _))
  }

}
