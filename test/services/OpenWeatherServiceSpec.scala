package services

import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito._
import org.scalatest.Inside.inside
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.Helpers.await

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class OpenWeatherServiceSpec extends PlaySpec with MockitoSugar with ForecastFixture  {

  val mockWS = mock[WSClient]
  val mockRequest = mock[WSRequest]
  val mockResponse = mock[WSResponse]

  val app = new GuiceApplicationBuilder()
    .overrides(bind[WSClient].toInstance(mockWS))
    .build()

  val service = app.injector.instanceOf(classOf[OpenWeatherService])


  "OpenWeatherService" should {

    "return forecast data" in {
      when(mockWS.url(anyString)).thenReturn(mockRequest)
      when(mockRequest.get()).thenReturn(Future.successful(mockResponse))
      when(mockResponse.status).thenReturn(200)
      when(mockResponse.json).thenReturn(Json.toJson(forecast))

      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Right(f) =>
        f mustBe forecast
      }
    }

    "fail gracefully if error encountered during deserialization" in {
      when(mockWS.url(anyString)).thenReturn(mockRequest)
      when(mockRequest.get()).thenReturn(Future.successful(mockResponse))
      when(mockResponse.status).thenReturn(200)
      when(mockResponse.json).thenReturn(invalidApiKeyJson)

      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Left(err) =>
        err.msg must include("JsError")
      }
    }

    "fail gracefully if api key is invalid" in {
      when(mockWS.url(anyString)).thenReturn(mockRequest)
      when(mockRequest.get()).thenReturn(Future.successful(mockResponse))
      when(mockResponse.status).thenReturn(401)
      when(mockResponse.json).thenReturn(invalidApiKeyJson)

      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Left(err) =>
        err.msg mustBe "Invalid api key"
      }
    }

    "fail gracefully if request data is invalid" in {
      when(mockWS.url(anyString)).thenReturn(mockRequest)
      when(mockRequest.get()).thenReturn(Future.successful(mockResponse))
      when(mockResponse.status).thenReturn(400)
      when(mockResponse.json).thenReturn(invalidInputJson)

      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Left(err) =>
        err.msg mustBe "please check entered values, nothing to geocode"
      }
    }

    "fail gracefully on not found" in {
      when(mockWS.url(anyString)).thenReturn(mockRequest)
      when(mockRequest.get()).thenReturn(Future.successful(mockResponse))
      when(mockResponse.status).thenReturn(404)

      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Left(err) =>
        err.msg mustBe "Houston, we have a problem"
      }
    }
  }
}