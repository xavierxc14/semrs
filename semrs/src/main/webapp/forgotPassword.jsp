<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
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

  <body>
<br />
<br />

  <div align="center" >
      <img alt="SEMRS" src="logo/semrs_logo.png"></img>
  </div>
<p></p>
<form id="loginform" name="f" action="/semrs/forgotPassword" method="post">
<fieldset>
<legend><b>Nueva Contraseña</b></legend> 
<%if (session.getAttribute("error")!=null) {%>
<div align="center"><font color="red"> 
<strong>
ERROR: <%= String.valueOf(session.getAttribute("error"))%>
</strong>
</font></div>
<% }%>
<%
session.removeAttribute("error");
%>
<label for='username'> 
  <input id='username' type='text' name='username'> 
  Usuario: 
</label> 
<label for='email'> 
  <input id='email' type='text' name='email'> 
  Email: 
</label> 
<label for="submit"> <input id="submit" name="submit" type="submit" value="Enviar"> 
</label>
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