<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <link rel="shortcut icon" href="/semrs/favicon.ico" />
    <title>SEMRS - Centro Integral de Salud Macuto</title>
<style type="text/css">
body,p,a,span,div,input,legend,h1,h2,h3,h4,h5,h6,li,dd,dt,th,td{
font-family:Arial, Helvetica, sans-serif;
}
body,p,a,span,div,input,legend,li,dd,dt,th,td{
font-size:10pt;
}
body {
background-color: #E8EEF7;
/*background-color: white;*/
color: black;
font-family: Arial, sans-serif;
font-size: smaller;
border: 0px;
margin: 0px;
}
#loginform {
width:300px;
margin:auto;
}
#loginform fieldset{
padding:10px;
}
#loginform legend{
font-weight:bold;
font-size:9pt;
}
#loginform label{
display:block;
height:2em;
/*background-color:#E7E7E7;*/
padding:10px 10px 0;
}
#loginform input {
margin-right:20px;
border:1px solid #999999;
float:right;
clear:right;
/*background:#CCCCCC;*/
}
#loginform input:focus,#loginform input:hover {
border:1px solid #333333;
}
.error{
color:red;
font-weight:bold;
}

</style>

</head>
  <body onload="document.f.j_username.focus();">
<br />
<br />
  <div align="center">
      <img alt="SEMRS" src="logo/semrs_logo.png"></img>
  </div>
<p></p>
<form id="loginform" name="f" action="<c:url value='j_spring_security_check'/>" method="post">
<fieldset>
<legend><b>Login</b></legend> 
<% if (session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null) { %>
<div align="center"><font color="red"> <strong>ERROR:
</strong>Usuario/Password invalido por favor intente de nuevo. <!-- Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>  -->
</font></div>
<% } %> 
<%if (String.valueOf(session.getAttribute("forgotpassword")).equals("success")) {%>
<div align="center"><font color="red"> 
<strong>
Su nuevo password ha sido enviado a su correo. 
</strong>
</font></div>
<% }else if (String.valueOf(session.getAttribute("forgotpassword")).equals("fail")){ %>
 <div align="center"><font color="red"> 
<strong>ERROR: </strong>
Ocurrio un error al enviar el nuevo password a su correo, por favor contacte con el administrador. 
</font></div>
<%} %>
<%
session.removeAttribute("forgotpassword");
%>
<label for='j_username'> 
  <input id='j_username' type='text' name='j_username'<% if (session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) != null) { %> value='<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>'<% } %>> 
  Usuario: 
</label> 
<label for='j_password'> 
  <input id='j_password' type='password' name='j_password'> 
  Password: 
</label> 
<label for="_spring_security_remember_me"> 
  <input id="_spring_security_remember_me" type="checkbox" name="_spring_security_remember_me"> 
  Recordarme: 
</label> 
<label for="submit"> <input id="submit" name="submit" type="submit" value="Login"> 
</label>
<p>
<div align = "center">
<a href="/semrs/forgotPassword.jsp">Olvido su contraseña?</a>
</div>
</fieldset>

<div style="margin-top: 5.5em;">
			<hr/>
			<span style="color: grey; font-size: 0.7em;">
				© 2008 Roger Marin, Jose Alvarado 
			</span>
			<br/>
		</div>
</form>

</body>
</html>
