package tictactoe.util;

import javax.servlet.http.HttpServletRequest;

import tictactoe.model.Player;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RequestParser {
	
	public static Player getPlayer(HttpServletRequest req) {
		Player p = null;
		try {
			p = new Gson().fromJson(Player.class.getName(), Player.class);
			return p;
		} catch (JsonSyntaxException e) {
			// TODO leave a log
		}
		return null;
	}

}
