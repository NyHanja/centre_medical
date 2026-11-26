<%@ page import="mg.centre.model.Patient" %>
<%
    Patient p = (Patient) request.getAttribute("patient");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon dossier</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<%@ include file="_barre.jspf" %>
<main>
    <h1>Mon dossier m&eacute;dical</h1>

    <p><strong>N&deg; patient :</strong> <%= p.getId() %></p>
    <p><strong>Nom :</strong> <%= Html.e(p.getNom()) %> <%= Html.e(p.getPrenom()) %></p>
    <p><strong>Date de naissance :</strong> <%= p.getDateNaissance() %> (<%= p.getAge() %> ans)</p>
    <p><strong>Sexe :</strong> <%= p.getSexe() %></p>
    <p><strong>T&eacute;l&eacute;phone :</strong> <%= Html.e(p.getTelephone()) %></p>
    <p><strong>Adresse :</strong> <%= Html.e(p.getAdresse()) %></p>
    <p><strong>Groupe sanguin :</strong> <%= Html.e(p.getGroupeSanguin()) %></p>
    <p><strong>Allergies :</strong> <%= Html.e(p.getAllergies()) %></p>

    <p><a href="<%= ctx %>/accueil">&larr; Accueil</a></p>
</main>
</body>
</html>