<%@ page import="mg.centre.model.Patient" %>
<%
    Patient p = (Patient) request.getAttribute("patient");
    String loginCompte = (String) request.getAttribute("loginCompte");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Fiche patient</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<%@ include file="_barre.jspf" %>
<main>
    <h1><%= Html.e(p.getNom()) %> <%= Html.e(p.getPrenom()) %></h1>

    <p><strong>N&deg; patient :</strong> <%= p.getId() %></p>
    <p><strong>Date de naissance :</strong> <%= p.getDateNaissance() %> (<%= p.getAge() %> ans)</p>
    <p><strong>Sexe :</strong> <%= p.getSexe() %></p>
    <p><strong>T&eacute;l&eacute;phone :</strong> <%= Html.e(p.getTelephone()) %></p>
    <p><strong>Adresse :</strong> <%= Html.e(p.getAdresse()) %></p>
    <p><strong>Groupe sanguin :</strong> <%= Html.e(p.getGroupeSanguin()) %></p>
    <p><strong>Allergies :</strong> <%= Html.e(p.getAllergies()) %></p>

    <a class="bouton" href="<%= ctx %>/patients/modifier?id=<%= p.getId() %>">Modifier</a>

    <form method="post" action="<%= ctx %>/patients/supprimer" style="display:inline">
        <input type="hidden" name="id" value="<%= p.getId() %>">
        <button type="submit" class="danger">Supprimer</button>
    </form>

    <h2>QR code</h2>
    <% String qrcodeImage = (String) request.getAttribute("qrcodeImage"); %>
    <% if (qrcodeImage != null) { %>
        <img src="data:image/jpeg;base64,<%= qrcodeImage %>" alt="QR code du patient" width="200" height="200">
    <% } else { %>
        <p>Pas encore de QR code.</p>
        <form method="post" action="<%= ctx %>/patients/qrcode">
            <input type="hidden" name="id" value="<%= p.getId() %>">
            <button type="submit">G&eacute;n&eacute;rer le QR code</button>
        </form>
    <% } %>

    <h2>Compte du patient</h2>
    <% if (loginCompte != null) { %>
        <p>Login : <strong><%= Html.e(loginCompte) %></strong></p>
    <% } else { %>
        <form method="post" action="<%= ctx %>/patients/compte">
            <input type="hidden" name="id" value="<%= p.getId() %>">
            <label>Login</label>
            <input type="text" name="login">
            <label>Mot de passe (8 caract&egrave;res minimum)</label>
            <input type="password" name="motDePasse">
            <button type="submit">Cr&eacute;er le compte</button>
        </form>
    <% } %>

    <p><a href="<%= ctx %>/patients">&larr; Liste des patients</a></p>
</main>
</body>
</html>