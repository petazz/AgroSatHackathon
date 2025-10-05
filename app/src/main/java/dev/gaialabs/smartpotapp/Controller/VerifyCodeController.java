package dev.gaialabs.smartpotapp.Controller;

import dev.gaialabs.smartpotapp.View.VerifyCodeActivity;

public class VerifyCodeController {
    private static VerifyCodeActivity myActivity;
    private static VerifyCodeController myController;

    private VerifyCodeController() {

    }

    public static VerifyCodeController getSingleton() {
        if (myController == null)
            myController = new VerifyCodeController();
        return myController;
    }

    public void setMyActivity(VerifyCodeActivity activity) {
        myActivity = activity;
    }

    public void makePetition(String code, String receivedEmail) {
        Peticion.getSingleton().verifyAccount(code, receivedEmail);
    }

    public void parseResponse(String res) {
        myActivity.onResponded(res);
    }

    public void makePetitionPassword(String code, String receivedEmail) {
    }
}
