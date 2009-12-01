<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%response.setHeader("Cache-Control", "no-cache");%>
<%response.setHeader("Pragma", "no-cache");%>

<%
  if(request.getUserPrincipal() == null){
	  response.sendRedirect("/semrs/");
  }

%>

<html>
<head>
    <title>SEMRS - Centro Integral de Salud Macuto</title>
    <!--CSS for loading message at application Startup-->

        <style type="text/css">
        @import url( "css/application.css" );
        @import url( "css/timeline.css" );
    </style>
    <style type="text/css">
        #loading {
            position: absolute;
            left: 45%;
            top: 40%;
            padding: 2px;
            z-index: 20001;
            height: auto;
            border: 1px solid #ccc;
        }
        #loading a {
            color: #225588;
        }

        #loading .loading-indicator {
            background: white;
            color: #444;
            font: bold 13px tahoma, arial, helvetica;
            padding: 10px;
            margin: 0;
            height: auto;
        }

        #loading-msg {
            font: normal 10px arial, tahoma, sans-serif;
        }
    </style>
    <link rel="stylesheet" href="semrs.css">
    <!--
    <script src="js/util/timeline-helper.js" type="text/javascript"></script>
    <script src="js/api/timeline-api.js" type="text/javascript"></script>-->
    <!-- script type="text/javascript" src="http://static.simile.mit.edu/timeplot/api/1.0/timeplot-api.js"></script> -->
</head>
  <link rel="shortcut icon" href="/semrs/favicon.ico" />
<body>
<iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

<!--add loading indicator while the app is being loaded-->
<div id="loading">
    <div class="loading-indicator">
        <img src="js/ext/resources/images/default/shared/large-loading.gif" width="32" height="32"
             style="margin-right:8px;float:left;vertical-align:top;"/>SEMRS <br/>
        <span id="loading-msg">Cargando imagenes...</span></div>
</div>

<!--include the Ext CSS, and use the gray theme-->
<link rel="stylesheet" type="text/css" href="js/ext/resources/css/ext-all.css"/>
<!-- link id="theme" rel="stylesheet" type="text/css" href="xtheme-default.css"/-->
<script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Cargando API...';</script>

<!--include the Ext Core API-->
<script type="text/javascript" src="js/ext/adapter/ext/ext-base.js"></script>

<!--include Ext -->
<script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Cargando Interfaz...';</script>
<script type="text/javascript" src="js/ext/ext-all.js"></script>
<script type="text/javascript" src="js/ext/source/locale/ext-lang-es.js"></script>
<script type="text/javascript" src="js/ext-ux-all-min.js"></script>
<!--include the application JS-->
<script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Inicializando...';</script>
<script type="text/javascript" src='com.googlecode.semrs.main.nocache.js'></script>

<!--hide loading message-->
<script type="text/javascript">Ext.get('loading').fadeOut({remove: true, duration:.25});</script>
</body>
</html>
