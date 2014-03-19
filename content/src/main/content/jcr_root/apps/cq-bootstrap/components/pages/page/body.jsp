<%@page session="false"%><%@include file="/apps/cq-bootstrap/global.jsp" %>
<%
    StringBuffer cls = new StringBuffer();
    for (String c: componentContext.getCssClassNames()) {
        cls.append(c).append(" ");
    }
%>
<body class="<%= cls %>">
<cq:include path="clientcontext" resourceType="cq/personalization/components/clientcontext"/>
<div class="container">
	<cq:include script="header.jsp"/>
    <cq:include script="content.jsp"/>
    <cq:include script="footer.jsp"/>
</div>
<cq:include path="cloudservices" resourceType="cq/cloudserviceconfigs/components/servicecomponents"/>
</body>
