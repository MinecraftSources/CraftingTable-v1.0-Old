<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <title>MN Squared</title>
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
    <!-- Optional theme -->
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css">
    <!-- Sticky Footer -->
    <link rel="stylesheet" href="/stylesheets/footer.css">
    <link rel="stylesheet" href="/stylesheets/template.css">

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script><!-- load jquery -->
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.22/angular.min.js"></script>

    <%
        String javascript = (String) request.getAttribute("javascript");
        if (javascript != null) {
    %>
    <script src="${pageContext.request.contextPath}/javascript/<%=javascript %>.js"></script>
    <%
        }
    %>
</head>