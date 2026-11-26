<%@ page import="mg.centre.model.Patient" %>
<%
    Patient p = (Patient) request.getAttribute("patient");
    boolean creation = (p.getId() == null);
    String ctx = request.getContextPath();
    String date = (p.getDateNaissance() == null) ? "" : p.getDateNaissance().toString();
    String sexe = (p.getSexe() == null) ? "" : p.getSexe();
    String groupe = (p.getGroupeSanguin() == null) ? "" : p.getGroupeSanguin();
    String[] groupes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><%= creation ? "Nouveau patient" : "Modifier le patient" %></title>
    <%@ include file="_style.jspf" %>
</head>
<body>
<%@ include file="_barre.jspf" %>
<main>
    <h1><%= creation ? "Nouveau patient" : "Modifier le patient" %></h1>

    <% if (request.getAttribute("erreur") != null) { %>
        <p class="alerte"><%= Html.e(request.getAttribute("erreur")) %></p>
    <% } %>

    <form method="post" action="<%= ctx %><%= creation ? "/patients/creer" : "/patients/maj" %>">
        <% if (!creation) { %>
            <input type="hidden" name="id" value="<%= p.getId() %>">
        <% } %>

        <label>Nom *</label>
        <input type="text" name="nom" value="<%= Html.e(p.getNom()) %>">

        <label>Pr&eacute;nom *</label>
        <input type="text" name="prenom" value="<%= Html.e(p.getPrenom()) %>">

        <label>Date de naissance *</label>
        <input type="date" name="dateNaissance" value="<%= date %>">

        <label>Sexe *</label>
        <select name="sexe">
            <option value="">-- Choisir --</option>
            <option value="M" <%= sexe.equals("M") ? "selected" : "" %>>Masculin</option>
            <option value="F" <%= sexe.equals("F") ? "selected" : "" %>>F&eacute;minin</option>
        </select>

        <label>T&eacute;l&eacute;phone</label>
        <input type="text" name="telephone" value="<%= Html.e(p.getTelephone()) %>">

        <label>Adresse</label>
        <input type="text" name="adresse" value="<%= Html.e(p.getAdresse()) %>">

        <label>Groupe sanguin</label>
        <select name="groupeSanguin">
            <option value="">-- Inconnu --</option>
            <% for (String g : groupes) { %>
                <option value="<%= g %>" <%= groupe.equals(g) ? "selected" : "" %>><%= g %></option>
            <% } %>
        </select>

        <label>Allergies</label>
        <textarea name="allergies" rows="3"><%= Html.e(p.getAllergies()) %></textarea>

        <button type="submit">Enregistrer</button>
        <a href="<%= ctx %>/patients">Annuler</a>
    </form>
</main>
</body>
</html>