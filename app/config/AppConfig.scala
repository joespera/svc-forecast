package config

import com.typesafe.config.Config
import play.api.Configuration

import javax.inject.{Inject, Provider}

//inject this class for access to global configuration
case class AppConfig(scheme: String, openWeatherHost: String, openWeatherPath: String, openWeatherApiKey: String)

object AppConfig {

  //constructor, takes typesafe config and produces our AppConfig model
  def apply(config: Config): AppConfig = {
    AppConfig(
      config.getString("openWeather.scheme"),
      config.getString("openWeather.host"),
      config.getString("openWeather.path"),
      config.getString("openWeather.apiKey")
    )
  }
}

//standard Guice pattern. Providers are objects capable of providing instances of type [T], in this case AppConfig
class AppConfigProvider @Inject()(config: Configuration) extends Provider[AppConfig] {
  override def get(): AppConfig = AppConfig(config.underlying)
}
