package com.micewine.emu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.micewine.emu.R;
import com.micewine.emu.core.WinetricksItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterWinetricks extends RecyclerView.Adapter<AdapterWinetricks.ViewHolder> {

    private final List<WinetricksItem> fullList;
    private List<WinetricksItem> filteredList;

    public AdapterWinetricks(List<WinetricksItem> list) {
        this.fullList = list;
        this.filteredList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_winetricks_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WinetricksItem item = filteredList.get(position);
        holder.nameText.setText(item.getName());
        holder.descriptionText.setText(item.getDescription());
        holder.categoryText.setText(item.getCategory());
        holder.checkBox.setChecked(item.isSelected());

        holder.itemView.setOnClickListener(v -> {
            item.setSelected(!item.isSelected());
            holder.checkBox.setChecked(item.isSelected());
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        if (query == null || query.isEmpty()) {
            filteredList = new ArrayList<>(fullList);
        } else {
            String lowerQuery = query.toLowerCase();
            filteredList = fullList.stream()
                    .filter(item -> item.getName().toLowerCase().contains(lowerQuery) ||
                            item.getDescription().toLowerCase().contains(lowerQuery) ||
                            item.getCategory().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public void updateList() {
        filter("");
    }

    public List<WinetricksItem> getSelectedItems() {
        return fullList.stream().filter(WinetricksItem::isSelected).collect(Collectors.toList());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, categoryText;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.winetricksItemName);
            descriptionText = itemView.findViewById(R.id.winetricksItemDescription);
            categoryText = itemView.findViewById(R.id.winetricksItemCategory);
            checkBox = itemView.findViewById(R.id.winetricksItemCheckBox);
        }
    }
}
