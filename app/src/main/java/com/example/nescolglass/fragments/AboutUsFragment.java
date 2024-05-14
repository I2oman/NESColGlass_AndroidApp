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
    private ImageButton projectbutton;
    private ImageButton bogdanbutton;
    private ImageButton oleksanderbutton;
    private ImageButton oliverbutton;
    private ImageButton romanbutton;

    private String whoweareText;
    private TextView whoweare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        // Inflate the layout for this fragment
        projectbutton = view.findViewById(R.id.projectButton);
        whoweare = view.findViewById(R.id.whatwedo);
        bogdanbutton = view.findViewById(R.id.bogdanButton);
        oleksanderbutton = view.findViewById(R.id.oleksanderButton);
        oliverbutton = view.findViewById(R.id.oliverButton);
        romanbutton = view.findViewById(R.id.romanButton);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.bogdanButton) {
                    openLinkInBrowser("https://github.com/Copciop23");
                } else if (v.getId() == R.id.oliverButton) {
                    openLinkInBrowser("https://github.com/OREN23");
                }else if (v.getId() == R.id.oleksanderButton) {
                    openLinkInBrowser("https://github.com/batreller");
                }else if (v.getId() == R.id.romanButton) {
                    openLinkInBrowser("https://github.com/I2oman");
                }else if (v.getId() == R.id.projectButton) {
                    openLinkInBrowser("https://github.com/I2oman/NESColGlass");
                }
            }
        };
        projectbutton.setOnClickListener(onClickListener);
        bogdanbutton.setOnClickListener(onClickListener);
        oliverbutton.setOnClickListener(onClickListener);
        oleksanderbutton.setOnClickListener(onClickListener);
        romanbutton.setOnClickListener(onClickListener);


        whoweareText = "We are a group of four inspired college students who decided on our group college project " +
                "to dream big and make a product that could be usable in the future. Driven by our shared, we set out to create " +
                "to create something groundbreaking for us. A glass device that seamlessly integrates phone features in your daily life like " +
                "providing you with notifications from your phone directly to your glasses, giving you directions based on where you wanna go by maps and other day to day features. ";
        whoweare.setText(whoweareText);


        return view;
    }
    private void openLinkInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
