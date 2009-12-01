<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<!-- Not used unless you declare a <form-login login-page="/login.jsp"/> element -->

<html>
  <head>
    <title>SEMRS</title>
  </head>

  <body onload="document.f.j_username.focus();">
    <h1>SEMRS</h1>

    <%-- this form-login-page form is also used as the
         form-error-page to ask for a login again.
         --%>
	<% if (session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null) { %>
      <font color="red">
        Usuario/Password invalido por favor intente de nuevo.<BR><BR>
       <!-- Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>  --> 
      </font>
    <% } %>

    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
      <table>
        <tr><td>Usuario:</td><td><input type='text' name='j_username' <% if (session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) != null) { %>value='<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>'<% } %>></td></tr>
        <tr><td>Password:</td><td><input type='password' name='j_password'></td></tr>
        <tr><td><input type="checkbox" name="_spring_security_remember_me"></td><td>Recordarme en este equipo</td></tr>

        <tr><td colspan='2'><input name="submit" type="submit" value="continuar"></td></tr>
        <tr><td colspan='2'><input name="reset" type="reset" value="borrar"></td></tr>
      </table>

    </form>

  </body>
</html>
