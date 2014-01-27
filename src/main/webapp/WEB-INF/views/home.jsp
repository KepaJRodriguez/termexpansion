<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h2>
	Hello world!  
</h2>

<P>  The time on the server is ${serverTime}. </P>

<P>  Commands </P>
<OL>
<LI> Old search: http://localhost:8080/search/search
<LI> Expansion service: http://localhost:8080/search/expand <br>
     If user gives more than one term results of the expansion of each terms are together in a list.<br>
     e.g. {"result": "[Paris, Parigi, Warschau,Varsovia]", "q":"Paris Warschau"<br>
     http://localhost:8080/search/expandquery.html?query=Paris+Warschau
<LI> Expansion service: http://localhost:8080/search/expandC<br>
e.g. {"result": "[[Paris, Parigi], [Warschau,Varsovia]]", "q":"Paris Warschau"
If user gives more than one term results of the expansion of each terms are separated in different lists.<br>
http://localhost:8080/search/expandqueryC.html?query=Paris+Warschau
</OL>
</body>
</html>
