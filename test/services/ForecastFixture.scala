package services

import errors.RetrievalError
import models.{Daily, Forecast, FormattedDay, FormattedForecast, Temp}
import play.api.libs.json.Json

trait ForecastFixture {
  val lat = 32.10394
  val long = -23.49922
  val units = "imperial"
  val error = RetrievalError("api key invalid")
  val forecast = Forecast(
    "America/Chicago",
    Seq(
      Daily(1647885600, Temp(51.6, 67.35)),
      Daily(1647972000, Temp(44.83, 66.94))
    )
  )
  val formatted = FormattedForecast(
    Seq(
      FormattedDay("MONDAY", "03/21/2022","51.6F", "67.35F"),
      FormattedDay("TUESDAY","03/22/2022","44.83F","66.94F")
    )
  )
  val invalidInputJson = Json.obj("cod" -> "400", "message" -> "wrong latitude")
  val invalidApiKeyJson = Json.obj("cod" -> 401, "message" -> "Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.")
}
