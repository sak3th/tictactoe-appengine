package tictactoe;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tictactoe.model.Player;
import tictactoe.model.PlayerEndpoint;
import tictactoe.model.Response;
import tictactoe.util.RequestParser;

/*
 * this servlet handles user logins, user details updation
 */
@SuppressWarnings("serial")
public class ReceptionServlet extends HttpServlet {
    
    private static final String DEVELOPMENT_LOGOUT_URI = "http://localhost:8888/logout/";
    private static final String PRODUCTION_LOGOUT_URI = "http://kattam-tictactoe.appspot.com";
    
    private static final Logger log = Logger.getLogger(ReceptionServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("New request - " + req.getRequestURI());
        
        UserService userService = UserServiceFactory.getUserService();
        User user =  userService.getCurrentUser();
        
        try {
            OAuthService oauth = OAuthServiceFactory.getOAuthService();
            user = oauth.getCurrentUser();
        } catch (OAuthRequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //resp.setContentType("text/html");
        //resp.getWriter().println("<h2>GAE - Integrating Google user account</h2>");

        if (user != null) {
            log.info(user.getEmail() + " logged in");
            // user is logged in
            // add the user to the datastore if not already present
            // TODO can be clubbed using createOrUpdate
            if(PlayerEndpoint.get(user.getEmail()) == null) {
                Player p = RequestParser.getPlayer(req);
                if(p != null) PlayerEndpoint.insert(p);
                else PlayerEndpoint.insert(new Player(user.getEmail(), user.getUserId()));
            } else {
                Player p = RequestParser.getPlayer(req);
                if(p != null) PlayerEndpoint.update(p);
            }
            
            // TODO add the user to the online users list

            // send the user information so we can cut down on bandwidth and
            // delay in fetching the details from the server
            // TODO find a neater way to form the URI
            resp.setContentType("application/json");
            if(SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
                resp.getWriter().print(Response.create(Response.Status.OK, 
                                        userService.createLogoutURL(PRODUCTION_LOGOUT_URI
                                                                         + user.getEmail()),
                                        PlayerEndpoint.get(user.getEmail())));
            } else {
                resp.getWriter().print(Response.create(Response.Status.OK, 
                                        userService.createLogoutURL(DEVELOPMENT_LOGOUT_URI
                                                                         + user.getEmail()),
                                        PlayerEndpoint.get(user.getEmail())));               
            }
            resp.getWriter().flush();
        } else {
            // user is not logged in
            if(req.getRequestURI().contains(PRODUCTION_LOGOUT_URI) || 
                    req.getRequestURI().contains(DEVELOPMENT_LOGOUT_URI)) {
                String[] splits = (req.getRequestURI().split("/"));
                String email = splits[splits.length-1];
                // TODO remove the user from the online users list
            } else {
                resp.getWriter().print(Response.create(Response.Status.OK,
                                        userService.createLoginURL(req.getRequestURI()), null));
            }
        }
    }
}
