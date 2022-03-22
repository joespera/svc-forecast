package services

import errors.RetrievalError
import models.FormattedForecast

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait ForecastService {
  def getForecast(lat: Double, lon: Double, units: String): Future[Either[RetrievalError, FormattedForecast]]
}

/**
 * This is a service layer primarily for data transformation. This class injects the OpenWeather Service, and transforms
 * the data to something more readable for a user
 */
class ForecastServiceImpl @Inject()
  (openWeatherService: OpenWeatherService)(implicit ec: ExecutionContext) extends ForecastService {

  /**
   * This function calls the OpenWeather service and maps the Forecast object to a FormattedForecast object using a
   * conversion function that lives in the FormattedForecast object
   */
  def getForecast(lat: Double, lon: Double, units: String): Future[Either[RetrievalError, FormattedForecast]] = {
    openWeatherService.getForecast(lat, lon, units) map { forecast =>
      forecast.map(f => FormattedForecast.fromForecast(f, units))
    }
  }

}
