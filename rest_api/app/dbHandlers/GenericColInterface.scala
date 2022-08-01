package dbHandlers

import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import traits.BSONable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GenericColInterface {

  def getItems[A](queryList: List[(String, String)], col: BSONCollection)
                 (implicit r: BSONDocumentReader[A]): Future[Option[A]] = {
    @scala.annotation.tailrec
    def combineQueries(queryList: List[(String, String)], acc: BSONDocument): BSONDocument =
      if (queryList.isEmpty) acc
      else combineQueries(queryList.tail, acc ++ BSONDocument((queryList.head))) // double paren is fucking important!

    val query = combineQueries(queryList, BSONDocument())
    col.find(query).one
  }

  def insertItem(item: BSONable, col: BSONCollection): Future[WriteResult] =
    col.insert.one(item.toBSON)

  def updateItem(id: String, item: BSONable, col: BSONCollection): Future[WriteResult] = {
    val selector = BSONDocument("_id" -> id)
    val modifier = BSONDocument("$set" -> item.toBSON)
    col.update.one(selector, modifier, upsert = false, multi = false)
  }

  def deleteItem(id: String, col: BSONCollection): Future[WriteResult] = {
    val selector = BSONDocument("_id" -> id)
    col.delete.one(selector)
  }

}
