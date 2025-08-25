<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<div class="parameter">
  <span class="name">Action:</span> <span class="value"><props:displayValue name="action" /></span>
</div>
<div class="parameter">
  <span class="name">Login:</span> <span class="value"><props:displayValue name="login" /></span>
</div>
<div class="parameter">
  <span class="name">API Key:</span> <span class="value">******</span>
</div>
<div class="parameter">
  <span class="name">Jaws API Base:</span> <span class="value"><props:displayValue name="baseUrl" /></span>
</div>
<div class="parameter">
  <span class="name">Project ID:</span> <span class="value"><props:displayValue name="projectId" /></span>
</div>
<div class="parameter">
  <span class="name">Environments:</span> <span class="value"><props:displayValue name="environments" /></span>
</div>
<div class="parameter">
  <span class="name">Version:</span> <span class="value"><props:displayValue name="version" /></span>
</div>