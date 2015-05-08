package simulations

import simulations.PerformanceSystemEnvironment

import scala.io.Source
import scala.util.matching.Regex
import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem

class GlobalPerformanceMethods {

  private val environment: _root_.simulations.PerformanceSystemEnvironment = new PerformanceSystemEnvironment()

  def urlWithAuth(path: String): String = {
    s"http://${environment.authData}@${environment.serverHost}:${environment.serverPort}$path"
  }

  def stageFeedUrlForPipeline(pipeline: String): String = {
    urlWithAuth(s"/go/api/pipelines/$pipeline/stages.xml")
  }

  def urlsForComparingPipelineStages(): ArrayBuffer[Map[String, String]] = {
    val urlMapForComparingPipelineStages: ArrayBuffer[Map[String, String]] = new ArrayBuffer[Map[String, String]](10)
    val pipelines: Array[String] = environment.usePipelines.split(",").map(_.trim).distinct
    for (i <- 0 until pipelines.length) {
      val maxCounter: Int = maxStageCounter(Source.fromURL(stageFeedUrlForPipeline(pipelines(i))).mkString, pipelines(i))
      var lowerCounter: Int = maxCounter - environment.compareCounterDifference.toInt
      lowerCounter = if (lowerCounter < 1) 1 else lowerCounter
      val compareURL = "/compare/%s/%s/with/%s".format(pipelines(i), lowerCounter.toString, maxCounter.toString)
      urlMapForComparingPipelineStages += Map("url" -> compareURL)
    }
    urlMapForComparingPipelineStages
  }

  def maxStageCounter(xmlContent: String, pipeline: String): Int = {
    val patternBeforeCounter = new Regex("(.*)http://" + environment.serverHost + ":" + environment.serverPort + "/go/pipelines/" + pipeline)
    val patternForCounterAfterFirstOperation = new scala.util.matching.Regex("\\d")
    val xmlData = xml.XML.loadString(xmlContent)
    val xPaths = (xmlData \\ "feed" \ "entry" \ "id").map(_.text).head
    val counterOption: Option[String] = patternForCounterAfterFirstOperation findFirstIn (patternBeforeCounter replaceFirstIn(xPaths.mkString, "")).mkString
    val counter: Int = counterOption.get.toInt
    counter
  }

  def agentJobHistoryUrl(): ArrayBuffer[Map[String, String]] = {
    val agentJobHistoryUrl: ArrayBuffer[Map[String, String]] = new ArrayBuffer[Map[String, String]](10)
    val config: String = Source.fromURL(urlWithAuth("/go/api/admin/config.xml")).mkString
    val configXml: Elem = xml.XML.loadString(config)
    val uuid = (configXml \\ "agents" \ "agent" \ "@uuid").map(_.text).head
    agentJobHistoryUrl += Map("url" -> "/agents/%s/job_run_history".format(uuid.toString))
    agentJobHistoryUrl
  }
}