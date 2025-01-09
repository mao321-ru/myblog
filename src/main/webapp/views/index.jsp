<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Блог</title>
    <link rel="stylesheet" href="./pages/index.css">
</head>
<body>
<h2><%= "Привет!" %></h2>
<div class="footer">
    <p class="footer__generated">Generated: <%=request.getAttribute("generatedTime")%></p>
</div>
</body>
</html>
