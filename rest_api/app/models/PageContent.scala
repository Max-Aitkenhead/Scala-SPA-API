package models

import reactivemongo.api.bson.BSONDocument
import traits.BSONable

case class PageContent(id: String, name: String, path: String, markup: String, css: String, javascript: String, access: String) extends BSONable {

  override def toBSON: BSONDocument = BSONDocument(
    "_id" -> this.id,
    "name" -> this.name,
    "path" -> this.path,
    "markup" -> this.markup,
    "css" -> this.css,
    "javascript" -> this.javascript,
    "access" -> this.access
  )

  def toJson(): String = s""""pagejson":{"name":"${this.name}","path":"${this.path}", "markup":"${this.markup}", "css":"${this.css}", "js":"${this.javascript}"}"""
}


