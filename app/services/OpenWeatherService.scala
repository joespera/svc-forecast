package services

import config.AppConfig
import errors.RetrievalError
import models.Forecast
import play.api.http.Status.{BAD_REQUEST, OK, UNAUTHORIZED}
import play.api.libs.json.{JsError, JsSuccess}
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait OpenWeatherService {
  def getForecast(lat: Double, lon: Double, units: String): Future[Either[RetrievalError, Forecast]]
}

class OpenWeatherServiceImpl @Inject()
  (appConfig: AppConfig, ws: WSClient)(implicit ec: ExecutionContext) extends OpenWeatherService {

  //pull our config values from the injected app config
  private val host = appConfig.openWeatherHost
  private val path = appConfig.openWeatherPath
  private val key = appConfig.openWeatherApiKey
  private val openWeatherBaseUrl = s"$host$path?appid=$key" //build our base url with the apiKey

  /**
   * Function that reaches out to OpenWeather and retrieves weather data. Return value will either be unformatted forecast
   * data or a RetrievalError object. This is a pattern I like to follow instead of depending on exception handling for
   * flow, with the added benefit of passing along data as to why an error occurred. I chose to not do formatting on
   * data here as this is strictly a service used for communication. Data transformation is done in ForecastService,
   * which injects this class.
   */
  def getForecast(lat: Double, lon: Double, units: String): Future[Either[RetrievalError, Forecast]] = {
    val excludeFields = Seq("alerts", "hourly", "minutely", "current").mkString(",") //make comma delimited list of exclusion fields
    val forecastUrl = s"$openWeatherBaseUrl&lat=$lat&lon=$lon&units=$units&exclude=$excludeFields" //build our full url

    //make a GET request, case match based on response statuses from api
    //we could pass along error messaging based on the returned json from the openweather api, I am quickly, manually doing it
    ws.url(forecastUrl).get() map { response =>
      response.status match {
        case OK => response.json.validate[Forecast] match {
          case forecast: JsSuccess[Forecast] =>
            Right(forecast.get)
          case error: JsError =>
            Left(RetrievalError(error.toString))
        }
        case UNAUTHORIZED => Left(RetrievalError("Invalid api key"))
        case BAD_REQUEST => Left(RetrievalError("please check entered values, nothing to geocode"))
        case _ => Left(RetrievalError("Houston, we have a problem")) //catch all for 404s/500s
      }
    } recover {
      case NonFatal(e) => Left(RetrievalError(e.getMessage)) //usually if communication with the service cannot be made
    }
  }
}
