package dev.gaialabs.smartpotapp.Controller.RecyclerViews;

import dev.gaialabs.smartpotapp.Controller.Peticion;
import dev.gaialabs.smartpotapp.View.ResetPasswordActivity;

public class ResetPasswordController {
    private static ResetPasswordController myController;
    private static ResetPasswordActivity myActivity;

    private ResetPasswordController() {

    }

    public static ResetPasswordController getSingleton() {
        if (myController == null)
            myController = new ResetPasswordController();
        return myController;
    }

    public void setMyActivity(ResetPasswordActivity activity) {
        myActivity = activity;
    }

    public void makePetition(String password, String userEmail) {
        Peticion.getSingleton().restablecerPass(password, userEmail);
    }

    public void parseResponse(String res) {
        myActivity.changeIntent(res);
    }
}
