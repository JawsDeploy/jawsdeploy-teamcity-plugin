package com.jawsdeploy

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import jetbrains.buildServer.serverSide.ProjectManager
import org.jdom.Element
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JawsDeployController(
  private val web: WebControllerManager,
  private val descriptor: PluginDescriptor,
  private val client: JawsDeployClient,
  private val projects: ProjectManager
) : BaseController() {
  init { web.registerController("/jawsDeploy/action.html", this) }
  override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
    return when (request.getParameter("mode")) {
      "createDeploy" -> handleCreateDeploy(request, response)
      "promote" -> handlePromote(request, response)
      else -> { response.sendError(400, "Unknown mode"); null }
    }
  }
  private fun handleCreateDeploy(req: HttpServletRequest, resp: HttpServletResponse): ModelAndView? {
    val cfg = cfgFromReq(req)
    val projectId = req.getParameter("projectId")
    val version = req.getParameter("version")
    val environments = req.getParameter("environments")
      ?.split('\n', ',', ';')
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
    require(!projectId.isNullOrBlank() && !version.isNullOrBlank())
    val releaseId = client.createRelease(cfg, projectId, version)
    val depIds = client.deployRelease(cfg, releaseId, environments = environments)
    writeXml(resp, Element("result").apply {
      addContent(Element("releaseId").setText(releaseId))
      addContent(Element("deploymentIds").setText(depIds.joinToString(",")))
    })
    return null
  }
  private fun handlePromote(req: HttpServletRequest, resp: HttpServletResponse): ModelAndView? {
    val cfg = cfgFromReq(req)
    val projectId = req.getParameter("projectId")
    val version = req.getParameter("version")?.ifBlank { null }
    val environments = req.getParameter("environments")
      ?.split('\n', ',', ';')
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
    require(!projectId.isNullOrBlank())
    val depIds = client.promote(cfg, projectId, version, environments = environments)
    writeXml(resp, Element("result").apply {
      addContent(Element("deploymentIds").setText(depIds.joinToString(",")))
    })
    return null
  }
  private fun cfgFromReq(req: HttpServletRequest): JawsDeployClient.Config {
    val login = req.getParameter("login") ?: error("login required")
    val apiKey = req.getParameter("apiKey") ?: error("apiKey required")
    val baseUrl = req.getParameter("baseUrl")?.ifBlank { null } ?: "https://app.jawsdeploy.net/api"
    return JawsDeployClient.Config(baseUrl = baseUrl, login = login, apiKey = apiKey)
  }
  private fun writeXml(resp: HttpServletResponse, elem: Element) {
    resp.contentType = "application/xml"
    val doc = org.jdom.Document(elem)
    val out = org.jdom.output.XMLOutputter(org.jdom.output.Format.getPrettyFormat())
    resp.writer.use { out.output(doc, it) }
  }
}