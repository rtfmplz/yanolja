package model

import java.sql.Timestamp
import java.text.SimpleDateFormat

import play.api.libs.json._


/**
  *
  * @param business_id
  * @param date
  * @param review
  */
case class Review(business_id: String, date: Timestamp, review: String)

object Review {

  implicit object timestampFormat extends Format[Timestamp] {

    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val reviewFormat: OFormat[Review] = Json.format[Review]
}
