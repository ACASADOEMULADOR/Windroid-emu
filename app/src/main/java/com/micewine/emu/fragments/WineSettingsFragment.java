package com.micewine.emu.fragments;

import static com.micewine.emu.activities.GeneralSettingsActivity.SEEKBAR;
import static com.micewine.emu.activities.GeneralSettingsActivity.SPINNER;
import static com.micewine.emu.activities.GeneralSettingsActivity.WINE_DPI;
import static com.micewine.emu.activities.GeneralSettingsActivity.WINE_DPI_DEFAULT_VALUE;
import static com.micewine.emu.activities.GeneralSettingsActivity.CPU_BACKEND;
import static com.micewine.emu.activities.GeneralSettingsActivity.CPU_BACKEND_BOX64;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.micewine.emu.R;
import com.micewine.emu.adapters.AdapterSettingsPreferences;
import com.micewine.emu.adapters.AdapterSettingsPreferences.SettingsListSpinner;

import java.util.ArrayList;

public class WineSettingsFragment extends Fragment {
    private RecyclerView recyclerView;
    private final ArrayList<SettingsListSpinner> settingsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_model, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewSettingsModel);

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.setSpanCount(1);
        }

        setAdapter();

        return rootView;
    }

    private void setAdapter() {
        recyclerView.setAdapter(new AdapterSettingsPreferences(settingsList, requireActivity()));

        settingsList.clear();

        addToAdapter(R.string.cpu_backend_title, R.string.cpu_backend_desc, new String[] { "Box64", "FexCore" }, null, SPINNER, "Box64", CPU_BACKEND);
        addToAdapter(R.string.wine_dpi, R.string.null_desc, null, new int[] { 96, 480 }, SEEKBAR, String.valueOf(WINE_DPI_DEFAULT_VALUE), WINE_DPI);
    }

    private void addToAdapter(int titleId, int descriptionId, String[] spinnerOptions, int[] seekBarValues, int type, String defaultValue, String keyId) {
        settingsList.add(
                new SettingsListSpinner(titleId, descriptionId, spinnerOptions, seekBarValues, type, defaultValue, keyId)
        );
    }
}