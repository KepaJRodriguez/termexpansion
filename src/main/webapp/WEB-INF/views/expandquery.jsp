<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>expansion</title>
</head>
<body>

<c:forEach items="${queryresults}" var="queryresult"> 
${queryresult.result}</p>
</c:forEach>
</body>
</html>