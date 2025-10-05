package dev.gaialabs.smartpotapp.Controller;

import android.widget.EditText;

import dev.gaialabs.smartpotapp.Model.User;
import dev.gaialabs.smartpotapp.View.MainActivity;

public class MainController {
    private static MainActivity myactivity;
    private static MainController controller;
    private final User user;

    private MainController ()
    {
        user = new User();
    }

    public User getUser() {
        return user;
    }

    public static MainController getSingleton() {
        if (controller == null)
            controller = new MainController();
        return controller;
    }

    public void setActivity(MainActivity activity)
    {
        myactivity = activity;
    }

    public void parseDataLogin(String body, String email) {
        User tempUser = null;
        if (body != null)
            tempUser = Respuesta.getSingleton().parseDataLogin(body);
        if (tempUser != null)
        {
            user.setUserDetails(tempUser);
            user.setEmail(email);;
            myactivity.goMainPage();
        }
    }

    public void setLoginError(String error) {
        myactivity.setLoginError(error);
    }

    public void makePetition(String email, String password) {
        Peticion.getSingleton().makeLogin(email, password);
    }

}
