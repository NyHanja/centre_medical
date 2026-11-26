<%@ page import="java.util.List, mg.centre.model.Patient" %>
<%
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Patients</title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<%@ include file="_barre.jspf" %>
<main>
    <h1>Patients</h1>
    <a class="bouton" href="<%= ctx %>/patients/nouveau">+ Nouveau patient</a>

    <table>
        <tr>
            <th>N&deg;</th><th>Nom</th><th>Pr&eacute;nom</th><th>&Acirc;ge</th><th>Sexe</th><th></th>
        </tr>
        <% for (Patient p : patients) { %>
        <tr>
            <td><%= p.getId() %></td>
            <td><%= Html.e(p.getNom()) %></td>
            <td><%= Html.e(p.getPrenom()) %></td>
            <td><%= p.getAge() %> ans</td>
            <td><%= p.getSexe() %></td>
            <td><a href="<%= ctx %>/patients/fiche?id=<%= p.getId() %>">Voir</a></td>
        </tr>
        <% } %>
    </table>

    <p><a href="<%= ctx %>/accueil">&larr; Accueil</a></p>
</main>
</body>
</html>