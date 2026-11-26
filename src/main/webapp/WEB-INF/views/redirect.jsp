<%
    String cible = (String) request.getAttribute("cible");
    response.sendRedirect(request.getContextPath() + cible);
%>