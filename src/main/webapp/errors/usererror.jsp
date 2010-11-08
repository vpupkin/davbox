<%
UserService userService = UserServiceFactory.getUserService();
String thisURL = request.getRequestURI();
%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%
//response.sendRedirect(userService.createLoginURL(request.getRequestURI()));

if (request.getUserPrincipal() != null) {
	String req_url =  ""+ request.getSession().getAttribute("req_url");
	if (! "null".equals( req_url) ){ // pop req url
		request.getSession().setAttribute("req_url", null);
		response.sendRedirect(req_url);
	}else{
			 %><p>Hello, <%=request.getUserPrincipal().getName()%> [request.getUserPrincipal()]! 
		   Looks like u have not enough permissions for <a href="<%=request.getRequestURL()%>">this site.
		   <% 
	}
%>
	   <br>
	   Hi <%=request.getUserPrincipal()%>!.<br>
	   You can <a href= "<%=userService.createLogoutURL(thisURL) %>">sign out.</a> and login with another account.</p> 
	   If you think something wrong  - contact with your Admin.</p> 
<%
}else{ 
	request.getSession().setAttribute("req_url", ""+request.getRequestURI());
%>
	<p>Please <a href="<%=userService.createLoginURL(thisURL) %>">sign in</a>.</p>
<%
}
%>