<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil - Centre m&eacute;dical</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<%@ include file="_barre.jspf" %>
<main>
    <h1>Bienvenue <%= Html.e(request.getAttribute("login")) %></h1>

    <% if ("MEDECIN".equals(request.getAttribute("role"))) { %>
        <p>Vous &ecirc;tes connect&eacute; en tant que <strong>m&eacute;decin</strong>.</p>
        <a class="bouton" href="<%= request.getContextPath() %>/patients">G&eacute;rer les patients</a>
    <% } else { %>
        <p>Vous &ecirc;tes connect&eacute; en tant que <strong>patient</strong>.</p>
        <a class="bouton" href="<%= request.getContextPath() %>/mon-dossier">Voir mon dossier</a>
    <% } %>
</main>
</body>
</html>