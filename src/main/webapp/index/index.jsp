<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" ng-app="dashboard">
    <jsp:include page="header.jsp" />
    <body>
    <jsp:include page="top-navbar.jsp" />

    <div class="container-fluid">
        <div class="row">
            <jsp:include page="left-navbar.jsp" />
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

                <jsp:include page="/partials/${partial}.jsp" />

            </div>
        </div>

    </div>
    <jsp:include page="footer.jsp" />
    <jsp:include page="bootstrap.jsp" />
    </body>
</html>