package simulations

class PerformanceSystemEnvironment {
  val serverHost: String = scala.util.Properties.envOrElse("SERVER_HOST", "localhost")
  val serverPort: String = scala.util.Properties.envOrElse("SERVER_PORT", "8153")
  val compareCounterDifference: String = scala.util.Properties.envOrElse("COMPARE_COUNTER_DIFFERENCE", "1")
  val usePipelines: String = scala.util.Properties.envOrElse("USE_PIPELINES", "P1,P2 ,P3  ")
  val authData: String = scala.util.Properties.envOrElse("AUTH_DATA", "username:password")
}