package model

import java.sql.Timestamp
import java.text.SimpleDateFormat
import play.api.libs.json._


/**
  *
  * @param business_id
  * @param name
  * @param address
  * @param city
  * @param latitude
  * @param longitude
  */
case class HipPlace(business_id: String, name: String, address: String,
                    city: String, latitude: String, longitude: String)

object HipPlace {

  implicit object timestampFormat extends Format[Timestamp] {

    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val hipPlaceFormat: OFormat[HipPlace] = Json.format[HipPlace]
}
