package dev.gaialabs.smartpotapp.Controller;

import dev.gaialabs.smartpotapp.View.ForgotPasswordActivity;

public class ForgotPasswordController {
    private static ForgotPasswordController myController;
    private static ForgotPasswordActivity myActivity;

    private ForgotPasswordController() {

    }

    public static ForgotPasswordController getSingleton() {
        if (myController == null)
            myController = new ForgotPasswordController();
        return  myController;
    }

    public void setMyActivity(ForgotPasswordActivity activity) {
        myActivity = activity;
    }

    public void makePetition(String email) {
        Peticion.getSingleton().forgotPassword(email);
    }

    public void setError(String res) {

    }

    public void parseResponse(String res) {
        myActivity.changeIntent(res);
    }
}
