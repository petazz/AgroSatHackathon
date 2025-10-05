package dev.gaialabs.smartpotapp.Controller.RecyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dev.gaialabs.smartpotapp.Model.PlantData;
import dev.gaialabs.smartpotapp.R;

public class DataFragmentAdapter extends RecyclerView.Adapter<DataFragmentViewHolder> {

    private final List<PlantData> dataPlants;
    private LayoutInflater mInflater;

    public DataFragmentAdapter(Context context, List<PlantData> plants) {
        mInflater = LayoutInflater.from(context);
        this.dataPlants = plants;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataPlants.size() % 2 != 0 && position == dataPlants.size() - 1) {
            return 2; // Ocupa todo el ancho
        }
        return 1; // Ocupa una columna
    }

    @NonNull
    @Override
    public DataFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = mInflater.inflate(R.layout.recycler_data_fragment_2, parent, false);
        } else {
            view = mInflater.inflate(R.layout.recycler_data_fragment, parent, false);
        }
        return new DataFragmentViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull DataFragmentViewHolder holder, int position) {
        PlantData plant = this.dataPlants.get(position);
        holder.setDataName(plant.getName());
        holder.setDataNumber(plant.getFormattedNumber());
    }


    @Override
    public int getItemCount() {
        return dataPlants.size();
    }

    public List<PlantData> getPlants() {
        return dataPlants;
    }
}
