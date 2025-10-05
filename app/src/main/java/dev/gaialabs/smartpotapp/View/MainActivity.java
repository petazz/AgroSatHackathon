package dev.gaialabs.smartpotapp.View;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.gaialabs.smartpotapp.Controller.InputFilterHelper;
import dev.gaialabs.smartpotapp.Controller.MainController;
import dev.gaialabs.smartpotapp.R;

public class   MainActivity extends AppCompatActivity {
    private MainActivity activity;
    private Button loginBtn;
    private EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        MainController.getSingleton().setActivity(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (checkNetworkConnection(this).equals("Sin Conexión"))
        {
            Toast.makeText(this, getString(R.string.main_no_internet), Toast.LENGTH_LONG).show();
            finishAndRemoveTask();
        }
        loginBtn = (Button) findViewById(R.id.loginBtn);
        email = (EditText) findViewById(R.id.user_et);
        EditText password = (EditText) findViewById(R.id.passwd_et);
        TextView fgtPasswd = (TextView) findViewById(R.id.fgtpasswd_txt);
        InputFilterHelper.blockCharacters(email, " ,;:<>()[\\]{}\\/|");
        InputFilterHelper.blockCharacters(password, " '\";<>--");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InputFilterHelper.validEmail(email.getText().toString()))
                {
                    Toast.makeText(activity, getString(R.string.bad_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().isEmpty())
                {
                    Toast.makeText(activity, getString(R.string.empty_passwd), Toast.LENGTH_SHORT).show();
                    return ;
                }
                MainController.getSingleton().makePetition(email.getText().toString(), password.getText().toString());
                loginBtn.setEnabled(false);
            }
        });
        fgtPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fgtPasswd.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public static String checkNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "Wi-Fi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "Datos Móviles";
            }
        }
        return "Sin Conexión";
    }

    public void setLoginError(String error) {
        int errorId;
        switch (error) {
            case "ACCOUNT_NOT_VERIFIED":
                errorId = R.string.account_not_verified;
                Intent i = new Intent(this, VerifyCodeActivity.class);
                i.putExtra("Verify", "as");
                i.putExtra("EMAIL", email.getText().toString());
                startActivity(i);
                break;
            default:
                errorId = R.string.unkonwn_error;
                break;
        }
        Toast.makeText(activity, getString(errorId), Toast.LENGTH_SHORT).show();
        loginBtn.setEnabled(true);
    }

    public void goMainPage() {
        Intent intent = new Intent(this, MainPageActivity.class);
        startActivity(intent);
        finish();
    }
}