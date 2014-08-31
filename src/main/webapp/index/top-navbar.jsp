<!-- Fixed navbar -->
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${pageContext.request.contextPath}/mn2">MN Squared</a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right">
                <%
                    String navActive = (String) request.getAttribute("navActive");
                %>

                <%
                    if (navActive.equals("home")) {
                %>
                <li class="active"><a href="${pageContext.request.contextPath}/mn2">Home</a></li>
                <%
                    } else {
                %>
                <li><a href="${pageContext.request.contextPath}/mn2">Home</a></li>
                <%
                    }
                %>

                <%
                    if (navActive.equals("bt")) {
                %>
                <li class="active"><a href="${pageContext.request.contextPath}/mn2/bungeetype/list">Bungee Types</a></li>
                <%
                    } else {
                %>
                <li><a href="${pageContext.request.contextPath}/mn2/bungeetype/list">Bungee Types</a></li>
                <%
                    }
                %>

                <%
                    if (navActive.equals("st")) {
                %>
                <li class="active"><a href="${pageContext.request.contextPath}/mn2/servertype/list">Server Types</a></li>
                <%
                    } else {
                %>
                <li><a href="${pageContext.request.contextPath}/mn2/servertype/list">Server Types</a></li>
                <%
                    }
                %>

                <%
                    if (navActive.equals("worlds")) {
                %>
                <li class="active"><a href="${pageContext.request.contextPath}/mn2/world/list">Worlds</a></li>
                <%
                    } else {
                %>
                <li><a href="${pageContext.request.contextPath}/mn2/world/list">Worlds</a></li>
                <%
                    }
                %>

                <%
                    if (navActive.equals("plugins")) {
                %>
                <li class="active"><a href="${pageContext.request.contextPath}/mn2/plugin/list">Plugins</a></li>
                <%
                    } else {
                %>
                <li><a href="${pageContext.request.contextPath}/mn2/plugin/list">Plugins</a></li>
                <%
                    }
                %>

                <%
                    if (navActive.equals("nodes")) {
                %>
                <li class="active"><a href="${pageContext.request.contextPath}/mn2/node/list">Nodes</a></li>
                <%
                    } else {
                %>
                <li><a href="${pageContext.request.contextPath}/mn2/node/list">Nodes</a></li>
                <%
                    }
                %>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</div>