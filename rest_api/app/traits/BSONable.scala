package traits

import reactivemongo.api.bson.BSONDocument

trait BSONable {
  def toBSON: BSONDocument
}
