package com.quizapp.shared;

import java.io.Serializable;

public class RequestData implements Serializable {
    private String action;
    private Object payload;

    public RequestData(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction() { return action; }
    public Object getPayload() { return payload; }
}
