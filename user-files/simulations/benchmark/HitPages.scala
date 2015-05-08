package simulations.benchmark

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import simulations.PerformanceSystemEnvironment
import simulations.GlobalPerformanceMethods
import scala.collection.mutable.ArrayBuffer

class HitPages extends Simulation {

  private val environment: _root_.simulations.PerformanceSystemEnvironment = new PerformanceSystemEnvironment()
  private val globalPerformanceMethods: _root_.simulations.GlobalPerformanceMethods = new GlobalPerformanceMethods()
  private val pipelineUrlsForComparingStages: ArrayBuffer[Map[String, String]] = globalPerformanceMethods.urlsForComparingPipelineStages()
  private val agentJobHistoryUrl = globalPerformanceMethods.agentJobHistoryUrl().circular
  private val feederForPipelineCompare = pipelineUrlsForComparingStages.circular

  val httpConf = http
    .baseURL(s"http://${environment.serverHost}:8153/go")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val headers_10 = Map("Content-Type" -> """application/x-www-form-urlencoded""")


  //TODO: hit_latest_stages
  //TODO: trigger_artifact_generation
  //TODO: hit_details_api

  val scnHitPages = scenario("Hitting various pages")
    .exec(http("home page")
    .get("/"))
    .exec(http("agents page")
    .get("/agents"))
    .exec(http("environments page")
    .get("/environments"))
    .exec(http("admin pipelines page")
    .get("/admin/pipelines"))
    .exec(http("admin templates page")
    .get("/admin/templates"))
    .exec(http("admin users page")
    .get("/admin/users"))
    .exec(http("admin server sonfiguration page")
    .get("/admin/config/server"))
    .exec(http("serer messages")
    .get("/server/messages.json"))
    .exec(http("cctray")
    .get("/cctray.xml"))
    .feed(feederForPipelineCompare)
    .exec(http("compare pipeline - ${url}")
    .get("${url}"))
    .feed(agentJobHistoryUrl)
    .exec(http("agent job history page")
    .get("${url}"))


  //  setUp(scnHitPages.inject(nothingFor(10 seconds), atOnceUsers(100)).protocols(httpConf))
  setUp(scnHitPages.inject(atOnceUsers(pipelineUrlsForComparingStages.length)).protocols(httpConf))
}
