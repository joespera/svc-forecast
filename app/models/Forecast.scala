package models

import play.api.libs.json.Json

case class Temp(min: Double, max: Double)

object Temp {
  implicit val tempFormat = Json.format[Temp] //serdes helper
}

case class Daily(dt: Long, temp: Temp)

object Daily {
  implicit val dailyFormat = Json.format[Daily] //serdes helper
}

case class Forecast(timezone: String, daily: Seq[Daily])

object Forecast {
  implicit val forecastFormat = Json.format[Forecast] //serdes helper
}
