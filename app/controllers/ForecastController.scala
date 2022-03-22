package controllers

import errors.RetrievalError
import forms.ForecastInputs
import forms.ForecastInputs.forecastForm
import models.FormattedForecast
import play.api.i18n.I18nSupport

import javax.inject._
import play.api.mvc._
import services.ForecastService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ForecastController @Inject()
(service: ForecastService)(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController with I18nSupport {

  /**
   * load template that includes a form to enter lat, long, and units
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(forecastForm))
  }

  /**
   * callback for form submission
   */
  def post() = Action.async { implicit request: Request[AnyContent] =>
    forecastForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.index(formWithErrors)))
      },
      formData => {
        service.getForecast(formData.latitude.doubleValue, formData.longitude.doubleValue, formData.units) map {
          case Left(error) =>
            BadRequest(
              views.html.index(
                ForecastInputs.forecastForm.fill(
                  ForecastInputs.ForecastData(formData.latitude, formData.longitude, formData.units)
                ),
                Some(error.msg)
              )
            )
          case Right(forecast) => Ok(views.html.forecast(forecast))
        }
      }
    )
  }
}
