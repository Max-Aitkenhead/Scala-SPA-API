package models


case class WriteRes(successful: Boolean, nRecordsUpdated: Int, errorMsg: String = "no error"){
  def toJson(): String =
    s""""writeResult":{"successful":$successful, "noRecords":$nRecordsUpdated}"""
}
