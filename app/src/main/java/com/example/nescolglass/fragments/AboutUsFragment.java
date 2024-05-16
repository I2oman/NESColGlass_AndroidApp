package com.example.nescolglass.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nescolglass.R;

public class AboutUsFragment extends Fragment {
    // UI elements
    private ImageButton projectbutton;
    private ImageButton bogdanbutton;
    private ImageButton oleksanderbutton;
    private ImageButton oliverbutton;
    private ImageButton romanbutton;
    private TextView whoweare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        // Initialize UI elements
        projectbutton = view.findViewById(R.id.projectButton);
        whoweare = view.findViewById(R.id.whatWeDo);
        bogdanbutton = view.findViewById(R.id.bogdanButton);
        oleksanderbutton = view.findViewById(R.id.oleksanderButton);
        oliverbutton = view.findViewById(R.id.oliverButton);
        romanbutton = view.findViewById(R.id.romanButton);

        // Set onClick listeners for each button
        projectbutton.setOnClickListener(this::onClick);
        bogdanbutton.setOnClickListener(this::onClick);
        oliverbutton.setOnClickListener(this::onClick);
        oleksanderbutton.setOnClickListener(this::onClick);
        romanbutton.setOnClickListener(this::onClick);

        return view;
    }

    // Method to handle button clicks
    public void onClick(View v) {
        if (v.getId() == R.id.bogdanButton) {
            // Open Bogdan's GitHub profile
            openLinkInBrowser("https://github.com/Copciop23");
        } else if (v.getId() == R.id.oliverButton) {
            // Open Oliver's GitHub profile
            openLinkInBrowser("https://github.com/OREN23");
        } else if (v.getId() == R.id.oleksanderButton) {
            // Open Oleksander's GitHub profile
            openLinkInBrowser("https://github.com/batreller");
        } else if (v.getId() == R.id.romanButton) {
            // Open Roman's GitHub profile
            openLinkInBrowser("https://github.com/I2oman");
        } else if (v.getId() == R.id.projectButton) {
            // Open project's GitHub profile
            openLinkInBrowser("https://github.com/I2oman/NESColGlass");
        }
    }

    // Method to open a URL in the default browser
    private void openLinkInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
