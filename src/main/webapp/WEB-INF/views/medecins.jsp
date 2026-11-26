<%@ page import="java.util.List, mg.centre.model.Utilisateur" %>
<%
    List<Utilisateur> medecins = (List<Utilisateur>) request.getAttribute("medecins");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>M&eacute;decins</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<%@ include file="_barre.jspf" %>
<main>
    <h1>M&eacute;decins</h1>

    <% if (request.getAttribute("message") != null) { %>
        <p class="succes"><%= Html.e(request.getAttribute("message")) %></p>
    <% } %>
    <% if (request.getAttribute("erreur") != null) { %>
        <p class="alerte"><%= Html.e(request.getAttribute("erreur")) %></p>
    <% } %>

    <table>
        <tr><th>N&deg;</th><th>Login</th></tr>
        <% for (Utilisateur m : medecins) { %>
        <tr>
            <td><%= m.getId() %></td>
            <td><%= Html.e(m.getLogin()) %></td>
        </tr>
        <% } %>
    </table>

    <h2>Ajouter un m&eacute;decin</h2>
    <form method="post" action="<%= ctx %>/medecins/creer">
        <label>Login</label>
        <input type="text" name="login">

        <label>Mot de passe (8 caract&egrave;res minimum)</label>
        <input type="password" name="motDePasse">

        <label>Confirmer le mot de passe</label>
        <input type="password" name="confirmation">

        <button type="submit">Cr&eacute;er le compte</button>
    </form>

    <p><a href="<%= ctx %>/accueil">&larr; Accueil</a></p>
</main>
</body>
</html>