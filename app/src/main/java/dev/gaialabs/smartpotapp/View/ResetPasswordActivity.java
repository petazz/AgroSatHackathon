package dev.gaialabs.smartpotapp.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import dev.gaialabs.smartpotapp.Controller.RecyclerViews.ResetPasswordController;
import dev.gaialabs.smartpotapp.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEt, confirmPasswordEt;
    private Button resetPasswordBtn;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ResetPasswordController.getSingleton().setMyActivity(this);
        // Inicializar vistas
        newPasswordEt = findViewById(R.id.new_password_et);
        confirmPasswordEt = findViewById(R.id.confirm_password_et);
        resetPasswordBtn = findViewById(R.id.reset_password_btn);

        // Obtener email del Intent anterior
        userEmail = getIntent().getStringExtra("EMAIL") != null ?
                getIntent().getStringExtra("EMAIL") : "";

        // Configurar listener del botón
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndResetPassword();
            }
        });
    }

    private void validateAndResetPassword() {
        String newPassword = newPasswordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            newPasswordEt.requestFocus();
            return;
        }

        // Validar longitud mínima de contraseña
        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            newPasswordEt.requestFocus();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            confirmPasswordEt.requestFocus();
            return;
        }

        // Validar fortaleza de contraseña (opcional)
        if (!isPasswordStrong(newPassword)) {
            Toast.makeText(this, "Password must contain at least one uppercase letter, one lowercase letter, and one number", Toast.LENGTH_LONG).show();
            return;
        }

        // Llamar método para actualizar contraseña
        ResetPasswordController.getSingleton().makePetition(newPassword, userEmail);
    }

    private boolean isPasswordStrong(String password) {
        // Verificar que contenga al menos una mayúscula, una minúscula y un número
        return password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*");
    }

    public void changeIntent(String res) {
        int resId;
        switch (res) {
            case "FORGOT_PASSWORD_SUCCESS":
                resId = R.string.bad_email;
            default:
                resId = R.string.operation_success;
        }
        Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
