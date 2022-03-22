package forms

import play.api.data.Form
import play.api.data.Forms._

object ForecastInputs {

  val forecastForm = Form(
    mapping(
      "latitude" -> bigDecimal.verifying("latitude must fall between -90 and 90", a => a >= -90 && a <= 90),
      "longitude"  -> bigDecimal.verifying("longitude must fall between -180 and 180", a => a >= -180 && a <= 180),
      "units" -> text
    )(ForecastData.apply)(ForecastData.unapply)
  )

  case class ForecastData(latitude: BigDecimal, longitude: BigDecimal, units: String)
}
