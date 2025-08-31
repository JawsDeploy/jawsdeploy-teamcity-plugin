<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean" />

<table class="runnerFormTable">
  <tr class="groupingTitle">
    <td colspan="2">Connection Settings</td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.apiBaseUrl">API Base URL: <l:star/></label></th>
    <td>
      <props:textProperty name="jawsdeploy.apiBaseUrl" className="longField" />
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.login">Login (service account ID): <l:star/></label></th>
    <td>
      <props:textProperty name="jawsdeploy.login" className="longField" />
    </td>
  </tr>
  <tr>
    <th><label for="secure:jawsdeploy_apiKey">API Key: <l:star/></label></th>
    <td>
      <props:passwordProperty name="secure:jawsdeploy_apiKey" className="longField" />
    </td>
  </tr>

  <tr class="groupingTitle">
    <td colspan="2">Deployment Configuration</td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.operation">Operation</label></th>
    <td>
      <props:selectProperty name="jawsdeploy.operation">
        <props:option value="createAndDeploy">Create &amp; Deploy</props:option>
        <props:option value="promote">Promote</props:option>
      </props:selectProperty>
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.projectId">Project ID</label></th>
    <td>
      <props:textProperty name="jawsdeploy.projectId" className="longField" />
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.version">Version</label></th>
    <td>
      <props:textProperty name="jawsdeploy.version" />
      <div class="smallNote">Defaults to %build.number%</div>
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.channelName">Channel Name</label></th>
    <td>
      <props:textProperty name="jawsdeploy.channelName" />
    </td>
  </tr>
  <tr>
    <th></th>
    <td>
      <props:checkboxProperty name="jawsdeploy.ignoreDefaultChannel" />
      <label for="jawsdeploy.ignoreDefaultChannel">Ignore default channel</label>
    </td>
  </tr>

  <tr class="groupingTitle">
    <td colspan="2">Target Environment</td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.phaseName">Phase Name</label></th>
    <td>
      <props:textProperty name="jawsdeploy.phaseName" />
      <div class="smallNote">Or specify environments</div>
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.environments">Environments</label></th>
    <td>
      <props:textProperty name="jawsdeploy.environments" />
      <div class="smallNote">Comma separated</div>
    </td>
  </tr>

  <tr class="groupingTitle">
    <td colspan="2">Deployment Options</td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.notes">Release notes</label></th>
    <td>
      <props:multilineProperty name="jawsdeploy.notes" linkTitle="Release notes" rows="5" cols="60" />
    </td>
  </tr>
  <tr>
    <th></th>
    <td>
      <props:checkboxProperty name="jawsdeploy.redownloadPackages" />
      <label for="jawsdeploy.redownloadPackages">Redownload packages</label>
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.excludeStepNames">Exclude step names</label></th>
    <td>
      <props:textProperty name="jawsdeploy.excludeStepNames" />
      <div class="smallNote">Comma separated</div>
    </td>
  </tr>

  <tr class="groupingTitle">
    <td colspan="2">Advanced Settings</td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.pollIntervalMs">Poll interval (ms)</label></th>
    <td>
      <props:textProperty name="jawsdeploy.pollIntervalMs" />
    </td>
  </tr>
  <tr>
    <th><label for="jawsdeploy.requestTimeoutMs">Request timeout (ms)</label></th>
    <td>
      <props:textProperty name="jawsdeploy.requestTimeoutMs" />
    </td>
  </tr>
</table>