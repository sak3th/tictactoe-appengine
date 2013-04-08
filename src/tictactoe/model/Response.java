package tictactoe.model;

import com.google.gson.Gson;

public class Response {
    
    public enum Status { OK, WARNING, ERROR }
    
    public Status status;
    public String msg;
    public Object obj;
    
    private Response(Status status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }
    
    public static Response create(Status status, String msg, Object obj) {
        return new Response(status, msg, obj);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}