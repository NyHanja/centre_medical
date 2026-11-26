<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - Centre m&eacute;dical</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<main class="etroit">
    <h1>Centre m&eacute;dical</h1>

    <% if (request.getAttribute("erreur") != null) { %>
        <p class="alerte"><%= Html.e(request.getAttribute("erreur")) %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/login" autocomplete="off">
        <input type="hidden" name="csrf" value="<%= Html.e(request.getAttribute("csrf")) %>">

        <label for="login">Login</label>
        <input type="text" id="login" name="login" maxlength="50" required autofocus
               value="<%= Html.e(request.getAttribute("login")) %>">

        <label for="motDePasse">Mot de passe</label>
        <input type="password" id="motDePasse" name="motDePasse" maxlength="128" required>

        <button type="submit">Se connecter</button>
    </form>
</main>
</body>
</html>