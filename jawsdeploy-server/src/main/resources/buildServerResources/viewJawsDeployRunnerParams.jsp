<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<ul>
  <li>API Base URL: <props:displayProperty name="jawsdeploy.apiBaseUrl"/></li>
  <li>Login (service account ID): <props:displayProperty name="jawsdeploy.login"/></li>
  <li>API Key: ****</li>
  <li>Operation: <props:displayProperty name="jawsdeploy.operation"/></li>
  <li>Project ID: <props:displayProperty name="jawsdeploy.projectId"/></li>
  <li>Version: <props:displayProperty name="jawsdeploy.version"/></li>
  <li>Environments: <props:displayProperty name="jawsdeploy.environments"/></li>
</ul>