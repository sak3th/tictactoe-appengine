package tictactoe.model;

import com.google.gson.Gson;

public class Response<T> {
    
    public enum Status { OK, WARNING, ERROR }
    
    public Status status;
    public String msg;
    public T obj;
    
    public Response(Status status, String msg, T obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}