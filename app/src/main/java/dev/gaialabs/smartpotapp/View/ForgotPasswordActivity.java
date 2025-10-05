package dev.gaialabs.smartpotapp.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.gaialabs.smartpotapp.Controller.ForgotPasswordController;
import dev.gaialabs.smartpotapp.Controller.MainController;
import dev.gaialabs.smartpotapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText emailEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ForgotPasswordController.getSingleton().setMyActivity(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgot_password), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailEt = findViewById(R.id.email_et);
        Button sendCodeBtn = findViewById(R.id.send_code_btn);

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString().trim();

                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Ingresa un email válido", Toast.LENGTH_SHORT).show();
                    return;
                }
                ForgotPasswordController.getSingleton().makePetition(email);
            }
        });
    }

    public void changeIntent(String res) {
        Intent intent = new Intent(this, VerifyCodeActivity.class);
        intent.putExtra("EMAIL", emailEt.getText().toString().trim());
        startActivity(intent);
        finish();
    }
}
