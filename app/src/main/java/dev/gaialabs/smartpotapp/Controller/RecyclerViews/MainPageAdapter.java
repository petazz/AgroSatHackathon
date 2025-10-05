package dev.gaialabs.smartpotapp.Controller.RecyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.R;

public class MainPageAdapter extends RecyclerView.Adapter<MainPageViewHolder> {
    private final List<Plant> plants; // Lista de plantas
    private LayoutInflater mInflater;
    // Constructor: Recibe la lista de plantas
    public MainPageAdapter(Context context, List<Plant> plants) {
        mInflater = LayoutInflater.from(context);
        this.plants = plants;
    }

    // getItemViewType: Devuelve el tipo de vista para la posición actual
    @Override
    public int getItemViewType(int position) {
        // Si el número de ítems es impar y es el último ítem, usar el layout de dos columnas
        if (plants.size() % 2 != 0 && position == plants.size() - 1) {
            return 2; // Ocupa todo el ancho
        }
        return 1; // Ocupa una columna
    }


    // onCreateViewHolder: Crea y devuelve un ViewHolder
    @NonNull
    @Override
    public MainPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = mInflater.inflate(R.layout.recycler_main_page, parent, false); // Layout para una columna
        } else {
            view = mInflater.inflate(R.layout.recycler_main_page_2, parent, false); // Layout para dos columnas
        }
        return new MainPageViewHolder(view, this); // Pasar el listener al ViewHolder
    }

    // onBindViewHolder: Vincula los datos de un ítem a su ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MainPageViewHolder holder, int position) {
        holder.setPlant(this.plants.get(position));
        holder.setNombrePlant(this.plants.get(position).getName());
    }

    // getItemCount: Devuelve el número total de ítems
    @Override
    public int getItemCount() {
        return plants.size();
    }
}