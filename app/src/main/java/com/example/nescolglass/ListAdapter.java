package com.example.nescolglass;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private static RecyclerViewInterface recyclerViewInterface;
    private ArrayList<BluetoothDevice> list;


    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView, recyclerViewInterface);
    }


    public ListAdapter(ArrayList<BluetoothDevice> list, RecyclerViewInterface recyclerViewInterface) {
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNumber;
        private TextView itemName;
        private TextView itemAddress;

        public MyViewHolder(final View view, RecyclerViewInterface recyclerViewInterface) {
            super(view);
            itemNumber = view.findViewById(R.id.itemNumber);
            itemName = view.findViewById(R.id.itemName);
            itemAddress = view.findViewById(R.id.itemAddress);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int possition = getAdapterPosition();

                        if (possition != RecyclerView.NO_POSITION) {
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
        holder.itemNumber.setText(String.valueOf(position));
        holder.itemName.setText(list.get(position).getName());
        holder.itemAddress.setText(list.get(position).getAddress());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
