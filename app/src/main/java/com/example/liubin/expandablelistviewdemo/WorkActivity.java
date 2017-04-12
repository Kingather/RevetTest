package com.example.liubin.expandablelistviewdemo;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class WorkActivity extends AppCompatActivity {

    private FloatingActionMenu menuRed;
    private Handler mUIHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        menuRed = (FloatingActionMenu) findViewById(R.id.menu_red);

        menuRed.setClosedOnTouchOutside(true);
        menuRed.hideMenuButton(false);

        final FloatingActionButton programFab1 = new FloatingActionButton(this);
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText(getString(R.string.lorem_ipsum));
        programFab1.setImageResource(R.drawable.ic_edit);
        menuRed.addMenuButton(programFab1);
        programFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programFab1.setLabelColors(ContextCompat.getColor(WorkActivity.this, R.color.grey),
                        ContextCompat.getColor(WorkActivity.this, R.color.light_grey),
                        ContextCompat.getColor(WorkActivity.this, R.color.white_transparent));
                programFab1.setLabelTextColor(ContextCompat.getColor(WorkActivity.this, R.color.black));
            }
        });

        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuRed.isOpened()) {
                    Toast.makeText(WorkActivity.this, menuRed.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }

                menuRed.toggle(true);
            }
        });
        int delay = 400;
        mUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menuRed.showMenuButton(true);
            }
        }, delay);
    }

}
