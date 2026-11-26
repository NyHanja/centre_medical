<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Erreur - Centre m&eacute;dical</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<main class="etroit">
    <h1>Erreur</h1>
    <p class="alerte"><%= Html.e(request.getAttribute("message")) %></p>
    <a class="bouton" href="<%= request.getContextPath() %>/">Retour &agrave; l'accueil</a>
</main>
</body>
</html>