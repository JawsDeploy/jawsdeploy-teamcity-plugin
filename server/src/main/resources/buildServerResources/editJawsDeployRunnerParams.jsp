<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean" />
<jsp:useBean id="jdConsts" class="net.jawsdeploy.teamcity.shared.JawsDeployRunnerConstants"/>

<div class="parameter">
  <label for="${jdConsts.PARAM_API_BASE_URL}">API Base URL: <l:star/></label>
  <props:textProperty name="${jdConsts.PARAM_API_BASE_URL}" className="longField" />
</div>
<div class="parameter">
  <label for="jawsdeploy.login">Login (service account ID): <l:star/></label>
  <props:textProperty name="jawsdeploy.login" />
</div>
<div class="parameter">
  <label for="${jdConsts.PARAM_API_KEY}">API Key: <l:star/></label>
  <props:passwordProperty name="${jdConsts.PARAM_API_KEY}" />
</div>
<div class="parameter">
  <label for="jawsdeploy.operation">Operation</label>
  <props:selectProperty name="jawsdeploy.operation">
    <props:option value="createAndDeploy">Create &amp; Deploy</props:option>
    <props:option value="promote">Promote</props:option>
  </props:selectProperty>
</div>
<div class="parameter">
  <label for="jawsdeploy.projectId">Project ID</label>
  <props:textProperty name="jawsdeploy.projectId" />
</div>
<div class="parameter">
  <label for="jawsdeploy.version">Version</label>
  <props:textProperty name="jawsdeploy.version" />
  <div class="smallNote">Defaults to %build.number%</div>
</div>
<div class="parameter">
  <label for="jawsdeploy.phaseName">Phase Name</label>
  <props:textProperty name="jawsdeploy.phaseName" />
  <div class="smallNote">Or specify environments</div>
</div>
<div class="parameter">
  <label for="jawsdeploy.environments">Environments</label>
  <props:textProperty name="jawsdeploy.environments" />
  <div class="smallNote">Comma separated</div>
</div>
<div class="parameter">
  <label for="jawsdeploy.channelName">Channel Name</label>
  <props:textProperty name="jawsdeploy.channelName" />
</div>
<div class="parameter">
  <props:checkboxProperty name="jawsdeploy.ignoreDefaultChannel" />
  <label for="jawsdeploy.ignoreDefaultChannel">Ignore default channel</label>
</div>
<div class="parameter">
  <label for="jawsdeploy.notes">Notes</label>
  <props:multilineProperty name="jawsdeploy.notes" />
</div>
<div class="parameter">
  <props:checkboxProperty name="jawsdeploy.redownloadPackages" />
  <label for="jawsdeploy.redownloadPackages">Redownload packages</label>
</div>
<div class="parameter">
  <label for="jawsdeploy.excludeStepNames">Exclude step names</label>
  <props:textProperty name="jawsdeploy.excludeStepNames" />
  <div class="smallNote">Comma separated</div>
</div>
<div class="parameter">
  <label for="jawsdeploy.pollIntervalMs">Poll interval (ms)</label>
  <props:textProperty name="jawsdeploy.pollIntervalMs" />
</div>
<div class="parameter">
  <label for="jawsdeploy.requestTimeoutMs">Request timeout (ms)</label>
  <props:textProperty name="jawsdeploy.requestTimeoutMs" />
</div>
