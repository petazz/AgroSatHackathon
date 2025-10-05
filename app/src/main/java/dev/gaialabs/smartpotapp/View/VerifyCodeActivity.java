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

import dev.gaialabs.smartpotapp.Controller.VerifyCodeController;
import dev.gaialabs.smartpotapp.R;

public class VerifyCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code);
        VerifyCodeController.getSingleton().setMyActivity(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.verify_code_back), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText codeEt = findViewById(R.id.code_et);
        Button verifyBtn = findViewById(R.id.verify_btn);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receivedEmail = getIntent().getStringExtra("EMAIL") != null ?
                        getIntent().getStringExtra("EMAIL") : "";
                String code = codeEt.getText().toString().trim();
                String mode = getIntent().getStringExtra("Verify") != null ? getIntent().getStringExtra("Verify") : null;
                if (mode == null) {
                    if (code.length() != 6) {
                        Toast.makeText(VerifyCodeActivity.this, "Código inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    VerifyCodeController.getSingleton().makePetitionPassword(code, receivedEmail);
                } else {
                    if (code.length() != 8) {
                        Toast.makeText(VerifyCodeActivity.this, "Código inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    VerifyCodeController.getSingleton().makePetition(code, receivedEmail);
                }
            }
        });
    }

    public void onResponded(String a) {
        String receivedEmail = getIntent().getStringExtra("EMAIL") != null ?
                getIntent().getStringExtra("EMAIL") : "";
        String mode = getIntent().getStringExtra("Verify") != null ? getIntent().getStringExtra("Verify") : null;
        Intent intent;
        if (mode == null)
            intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
        else
            intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
        intent.putExtra("EMAIL", receivedEmail);
        startActivity(intent);
        finish();
    }
}
