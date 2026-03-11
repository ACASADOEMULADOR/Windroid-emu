package com.micewine.emu.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.micewine.emu.R;
import com.micewine.emu.core.WinetricksWrapper;

public class WinetricksFragment extends Fragment {

    private EditText winetricksCommandInput;
    private Button btnRunWinetricks;
    private Button btnOpenLogViewer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_winetricks, container, false);

        winetricksCommandInput = rootView.findViewById(R.id.winetricksCommandInput);
        btnRunWinetricks = rootView.findViewById(R.id.btnRunWinetricks);
        btnOpenLogViewer = rootView.findViewById(R.id.btnOpenLogViewer);

        btnRunWinetricks.setOnClickListener(v -> {
            String args = winetricksCommandInput.getText().toString().trim();
            if (!args.isEmpty()) {
                // Run winetricks in background so it doesn't block the UI
                new Thread(() -> {
                    WinetricksWrapper.winetricks(args);
                }).start();
            }
        });

        btnOpenLogViewer.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_content, new LogViewerFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    }
}
