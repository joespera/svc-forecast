package models

import play.api.libs.json.Json

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Instant, ZoneId}

case class FormattedDay(dayOfWeek: String, date: String, low: String, high: String)

object FormattedDay {
  implicit val forecastFormat = Json.format[FormattedDay] //serdes helper
}

//This is really just a container for FormattedDay
case class FormattedForecast(days: Seq[FormattedDay])

object FormattedForecast {
  implicit val forecastFormat = Json.format[FormattedForecast] //serdes helper

  /**
     conversion function from data from OpenWeather to how we want to display it
   */
  def fromForecast(forecast: Forecast, units: String): FormattedForecast = {
    // Parse the count of whole seconds since 1970-01-01T00:00Z into a `Instant` object, taking into account the timezone
    // Create a tuple that also holds our min and max temps
    val zdtTempTuple = forecast.daily map { day =>
      (Instant.ofEpochSecond(day.dt).atZone(ZoneId.of(forecast.timezone)), day.temp.min, day.temp.max)
    }

    //filter out weekends
    val noWeekends = zdtTempTuple.filterNot(zdt =>
      zdt._1.getDayOfWeek == DayOfWeek.SATURDAY || zdt._1.getDayOfWeek == DayOfWeek.SUNDAY
    )

    //map info to our new formatted objects
    FormattedForecast {
      noWeekends map { dateAndTemp =>
        val dayOfWeek = dateAndTemp._1.getDayOfWeek.toString
        val date = dateAndTemp._1.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        val minTemp = dateAndTemp._2 //min temp is the third item in the tuple
        val maxTemp = dateAndTemp._3 //max temp is the third item in the tuple

        val unitDisplay = unitConversion(units)
        //string interpolate our temps with their unit of measurement
        FormattedDay(dayOfWeek, date, s"${minTemp}$unitDisplay", s"${maxTemp}$unitDisplay")
      }
    }
  }

  private def unitConversion(unit: String): String = {
    unit match {
      case "imperial" => "F" //Fahrenheit
      case "metric" => "C" //Celsius
      case "standard" => "K" //Kelvin
    }
  }
}