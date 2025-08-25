<%@ page contentType="text/html;charset=UTF-8" %> <%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %> <%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean" />

<table class="runnerFormTable">
  <tr>
    <th><label for="action">Action:</label></th>
    <td>
      <props:selectProperty name="action" id="action">
        <props:option value="createDeploy">Create &amp; Deploy</props:option>
        <props:option value="promote">Promote</props:option>
      </props:selectProperty>
      <span class="error" id="error_action"></span>
    </td>
  </tr>
  <tr>
    <th>
      <label for="login">Login: <l:star /></label>
    </th>
    <td>
      <props:textProperty name="login" className="longField" />
      <span class="error" id="error_login"></span>
    </td>
  </tr>
  <tr>
    <th>
      <label for="secure:apiKey">API Key: <l:star /></label>
    </th>
    <td>
      <props:passwordProperty name="secure:apiKey" className="longField" />
      <span class="error" id="error_secure:apiKey"></span>
    </td>
  </tr>
  <tr>
    <th><label for="baseUrl">Jaws API Base:</label></th>
    <td>
      <props:textProperty name="baseUrl" className="longField" />
      <span class="error" id="error_baseUrl"></span>
    </td>
  </tr>
  <tr>
    <th>
      <label for="projectId">Project ID: <l:star /></label>
    </th>
    <td>
      <props:textProperty name="projectId" className="longField" />
      <span class="error" id="error_projectId"></span>
    </td>
  </tr>
  <tr>
    <th>
      <label for="environments">Environments: <l:star /></label>
    </th>
    <td>
      <props:textProperty name="environments" className="mediumField" />
      <span class="smallNote">Comma or newline separated</span>
      <span class="error" id="error_environments"></span>
    </td>
  </tr>
  <tr>
    <th>
      <label for="version">Version: <l:star /></label>
    </th>
    <td>
      <props:textProperty name="version" className="mediumField" />
      <span class="error" id="error_version"></span>
    </td>
  </tr>
</table>
