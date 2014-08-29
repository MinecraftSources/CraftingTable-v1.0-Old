<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
    <jsp:include page="header.jsp" />
    <body>
    <jsp:include page="top-navbar.jsp" />

    <div class="container-fluid">
            <div class="main">
                <jsp:include page="/partials/${partial}.jsp" />
            </div>

    </div>
    <jsp:include page="footer.jsp" />
    <jsp:include page="bootstrap.jsp" />
    </body>
</html>