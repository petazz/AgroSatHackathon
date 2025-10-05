package dev.gaialabs.smartpotapp.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dev.gaialabs.smartpotapp.R;
import dev.gaialabs.smartpotapp.databinding.ActivityMainBinding;
import dev.gaialabs.smartpotapp.databinding.ActivityPlantChatPageBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

public class PlantChatPageActivity extends AppCompatActivity {
    ActivityPlantChatPageBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlantChatPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new DataFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.chat)
                    replaceFragment(new ChatFragment());
                else if (itemId == R.id.data)
                    replaceFragment(new DataFragment());
                return true;
            }
        });
    }
    /**
     * Remplazar un fragmento por otro
     *
     *
     * @param fragment Con esta variable, cogemos el fragmento que queremos reemplazar.
     * El resultado que obtendremos, será que se habrá cambiado el fragmento actual, al que hemos mandado por parametro.
     * */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
