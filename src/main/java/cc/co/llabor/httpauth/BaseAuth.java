package cc.co.llabor.httpauth;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class BaseAuth extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();

        String thisURL = request.getRequestURI();
        if (request.getUserPrincipal() != null) {
            response.getWriter().println("<p>Hello, " 
            							 + request.getUserPrincipal().getName() 
                                         + "! ::" +request.getAuthType() 
                                         + request.getRemoteUser()
                                         + (request.isSecure() ? "SEUCURE!!":" no secure ") 
                                         + (request.isUserInRole("admin") ? "ADMIN !":"-(") 
                                         + "  You can <a href=\"" +
                                         userService.createLogoutURL(thisURL) +
                                         "\">sign out</a>.</p>");
        } else {
            response.getWriter().println("<p>Please <a href=\"" +
                                         userService.createLoginURL(thisURL) +
                                         "\">sign in</a>.</p>");
        }
    }
}