package dev.gaialabs.smartpotapp.Controller.RecyclerViews;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dev.gaialabs.smartpotapp.R;

public class DataFragmentViewHolder extends RecyclerView.ViewHolder {

    private TextView dataName;
    private TextView dataNumber;
    private ProgressBar progressBar;
    final DataFragmentAdapter mAdapter;

    public DataFragmentViewHolder(@NonNull View itemView, DataFragmentAdapter mAdapter) {
        super(itemView);
        this.mAdapter = mAdapter;
        dataName = itemView.findViewById(R.id.dataRequired);
        dataNumber = itemView.findViewById(R.id.numberData);
        progressBar = itemView.findViewById(R.id.progressBar);
    }

    public void setDataName(String name) {
        dataName.setText(name);
    }

    public void setDataNumber(String number) {
        dataNumber.setText(number);
    }
}
