package dev.gaialabs.smartpotapp.Controller.RecyclerViews;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import dev.gaialabs.smartpotapp.Controller.MainPageController;
import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.R;
import dev.gaialabs.smartpotapp.View.PlantChatPageActivity;

public class MainPageViewHolder extends RecyclerView.ViewHolder {
    private TextView plantName; // Referencia al TextView en el layout del ítem
    private Plant plant; // Referencia a la planta actual
    final MainPageAdapter mAdapter;
    // Constructor: Inicializa las referencias a los elementos de la interfaz
    public MainPageViewHolder(@NonNull View itemView, MainPageAdapter mAdapter) {
        super(itemView);
        this.mAdapter = mAdapter;
        plantName = itemView.findViewById(R.id.plantName); // Obtener el TextView
        plant = null;
        // Configurar el clic en el botón
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                MainPageController.getSingleton().setPlantData(plant);
                Intent intent = new Intent(context, PlantChatPageActivity.class);
                context.startActivity(intent);
            }
        });
    }
    public void setNombrePlant(String nombrePlant)
    {
        this.plantName.setText(nombrePlant);
    }
    public void setPlant(Plant plant)
    {
        this.plant = plant;
    }
}