package services

import org.mockito.Mockito._
import org.scalatest.Inside.inside
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.await

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class ForecastServiceSpec extends PlaySpec with MockitoSugar with ForecastFixture {

  val mockOpenWeatherService = mock[OpenWeatherService]

  val app = new GuiceApplicationBuilder()
    .overrides(bind[OpenWeatherService].toInstance(mockOpenWeatherService))
    .build()

  val service = app.injector.instanceOf(classOf[ForecastService])

  "ForecastService" should {

    "format data from OpenWeatherService" in {
      when(mockOpenWeatherService.getForecast(lat, long, units)) thenReturn (Future.successful(Right(forecast)))
      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Right(ff) =>
        ff mustBe formatted
      }
    }

    "not change error messaging from OpenWeatherService" in {
      when(mockOpenWeatherService.getForecast(lat, long, units)) thenReturn (Future.successful(Left(error)))
      val result = await(service.getForecast(lat, long, units))(3.seconds)
      inside(result) { case Left(err) =>
        err mustBe error
      }
    }
  }
}
