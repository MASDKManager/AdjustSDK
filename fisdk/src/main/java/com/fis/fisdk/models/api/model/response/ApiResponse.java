package com.fis.fisdk.models.api.model.response;

import java.io.Serializable;

public class ApiResponse  implements Serializable {
    private int Error;
    private String Description;
    private String MessageToShow;
    private String SessionID;
    private NextAction NextAction;

    public int getError() {
        return Error;
    }

    public void setError(int error) {
        Error = error;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMessageToShow() {
        return MessageToShow;
    }

    public void setMessageToShow(String messageToShow) {
        MessageToShow = messageToShow;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public NextAction getNextAction() {
        return NextAction;
    }

    public void setNextAction(NextAction nextAction) {
        NextAction = nextAction;
    }
}
