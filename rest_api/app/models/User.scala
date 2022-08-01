package models

import reactivemongo.api.bson.BSONDocument
import traits.BSONable

case class User(id: String, username: String, password: String, info: String, permissions: String) extends BSONable {
  def toStub(): UserStub = UserStub(this.username, this.info)

  override def toBSON: BSONDocument =
    BSONDocument(
      "_id" -> this.id,
      "Name" -> this.username,
      "Password" -> this.password,
      "Info" -> this.info,
      "Permissions" -> this.permissions
    )
}

case class UserStub(username: String, info: String) extends BSONable {
  def toUser(id: String, password: String, permissions: String): User = User(id, this.username, password, this.info, permissions)

  override def toBSON: BSONDocument =
    BSONDocument(
      "Name" -> this.username,
      "Info" -> this.info
    )

  def toJson(): String = s""""user":{"name":"${this.username}","info":"${this.info}"}"""

}

case class UserStubWP(username: String, password: String)


