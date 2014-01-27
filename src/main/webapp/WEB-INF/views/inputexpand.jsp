<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>EHRI expansion service</title>
</head>
<body>
<h2>EHRI expansion service</h2>
<form:form method="GET" action="expandquery.html">
 
    <table>
    <tr>
        <td><form:label path="query">Introduce search item</form:label></td>
        <td><form:input path="query" /></td>
    </tr>

    <tr>
        <td colspan="2">
            <input type="submit" value="Submit"/>
        </td>
    </tr>
</table> 
     
</form:form>


<c:forEach items="${queryresults}" var="queryresult"> 
	
${queryresult.result}
	
</c:forEach>





</body>
</html>