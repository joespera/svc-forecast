package modules

import com.google.inject.{AbstractModule, Singleton}
import config.{AppConfig, AppConfigProvider}
import services.{ForecastService, ForecastServiceImpl, OpenWeatherService, OpenWeatherServiceImpl}

/** Modules are just a collection of bindings for classes */
class ApplicationModule extends AbstractModule{

  override def configure(): Unit = {
    //Bind AppConfig to its Provider
    bind(classOf[AppConfig]).toProvider(classOf[AppConfigProvider]).in(classOf[Singleton])
    //Bind Forecast trait to its implementation
    bind(classOf[ForecastService]).to(classOf[ForecastServiceImpl]).in(classOf[Singleton])
    /** Bind OpenWeather trait to its implementation class. This is also an opportunity where you can bind the trait
     * to a mock implementation, that serves up canned data, so you could run/test the app without reaching out to
     * an actual api.
     */
    bind(classOf[OpenWeatherService]).to(classOf[OpenWeatherServiceImpl]).in(classOf[Singleton])
  }
}
