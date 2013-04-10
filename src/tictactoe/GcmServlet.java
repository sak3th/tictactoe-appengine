package tictactoe;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tictactoe.model.Player;
import tictactoe.model.PlayerEndpoint;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class GcmServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(GcmServlet.class.getName());
    
    private static final String REGISTER_URL   = "/gcm/register";
    private static final String UNREGISTER_URL = "/gcm/unregister";    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Sender sender = new Sender("AIzaSyDGCVkuZ_in_n66emyBkheA524aZfWtqqU");
        Message message = new Message.Builder().build();
        Result result = sender.send(
                message, PlayerEndpoint.get("example@example.com").getGcmRegId(), 5);
        log.info("GCM broadcast result " + result);
    }

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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

        if (user != null) {
            log.info(user.getEmail() + " requesting for " + req.getRequestURI());
            String regId = req.getParameter("regId");
            if(req.getRequestURI().equals(REGISTER_URL)) {
                if(checkNotEmpty(regId)) {
                    Player p = PlayerEndpoint.get(user.getEmail());
                    p.setGcmRegId(regId);
                    PlayerEndpoint.update(p);
                }
            } else if(req.getRequestURI().equals(UNREGISTER_URL)) {
                if(checkNotEmpty(regId)) {
                    Player p = PlayerEndpoint.get(user.getEmail());
                    p.setGcmRegId(null);
                    PlayerEndpoint.update(p);
                }
            } else {
                log.info("GCM request for non-supported url");
            }
        } else {
            // do not encourage any GCM registrations from unknown users
            log.info("GCM request from unknown user");
        }

    }
    
    private boolean checkNotEmpty(String regId) {
        return (regId != null && (!"".equals(regId)));
    }
}
