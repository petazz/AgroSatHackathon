package dev.gaialabs.smartpotapp.Controller;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Pattern;

public class InputFilterHelper {

    // Método para bloquear caracteres específicos en un EditText
    public static void blockCharacters(EditText editText, final String blockedChars) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && blockedChars.contains(source)) {
                    return ""; // Bloquear caracteres no permitidos
                }
                return null; // Permitir lo demás
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static boolean validEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
