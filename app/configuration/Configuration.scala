package configuration

import com.typesafe.config.{Config, ConfigFactory}

class Configuration(config: Config) {

}

object DefaultConfiguration extends Configuration(ConfigFactory.load())