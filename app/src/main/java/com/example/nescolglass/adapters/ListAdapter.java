package com.example.nescolglass.adapters;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nescolglass.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    // Interface to handle item click events
    private static RecyclerViewInterface recyclerViewInterface;
    // List to hold Bluetooth devices
    private ArrayList<BluetoothDevice> list;

    // Constructor to initialize the adapter with data and interface
    public ListAdapter(ArrayList<BluetoothDevice> list, RecyclerViewInterface recyclerViewInterface) {
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create the view holder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView, recyclerViewInterface);
    }

    // View holder class to hold the views of each item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNumber;
        private TextView itemName;
        private TextView itemAddress;

        public MyViewHolder(final View view, RecyclerViewInterface recyclerViewInterface) {
            super(view);
            // Initialize views
            itemNumber = view.findViewById(R.id.itemNumber);
            itemName = view.findViewById(R.id.itemName);
            itemAddress = view.findViewById(R.id.itemAddress);

            // Set click listener for the item
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        // Get the position of the clicked item
                        int possition = getAdapterPosition();
                        if (possition != RecyclerView.NO_POSITION) {
                            // Notify the interface about the item click
                            recyclerViewInterface.onItemClick(possition);
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {
        // Bind data to the views of the item
        holder.itemNumber.setText(String.valueOf(position));
        holder.itemName.setText(list.get(position).getName());
        holder.itemAddress.setText(list.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return list.size();
    }
}
