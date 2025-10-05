package dev.gaialabs.smartpotapp.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import dev.gaialabs.smartpotapp.Controller.DataFragController;
import dev.gaialabs.smartpotapp.Controller.RecyclerViews.DataFragmentAdapter;
import dev.gaialabs.smartpotapp.Model.PlantData;
import dev.gaialabs.smartpotapp.R;

public class DataFragment extends Fragment {
    private RecyclerView plantsRecyclerView;
    private DataFragmentAdapter plantAdapter;
    private List<PlantData> plantData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment, container, false);
        DataFragController.getSingleton().setFragment(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataFragController.getSingleton().analizePlantData();
    }

    public void setupRecycler() {
        plantData = DataFragController.getSingleton().getAnalizedData();
        View view = getView();
        plantsRecyclerView = view.findViewById(R.id.rvDataPlant);
        plantAdapter = new DataFragmentAdapter(getContext(), plantData);
        plantsRecyclerView.setAdapter(plantAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        plantsRecyclerView.setLayoutManager(gridLayoutManager);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (plantData.size() % 2 != 0 && position == plantData.size() - 1) {
                    return 2;
                }
                return 1;
            }
        });
    }
}
