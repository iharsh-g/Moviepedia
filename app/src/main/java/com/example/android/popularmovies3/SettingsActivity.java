package com.example.android.popularmovies3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.android.popularmovies3.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivitySettingsBinding mBinding;
    private final String[] imageQuality = {"Low", "Medium", "High", "Maximum"};
    private SharedPreferences mSharedPreferences;

    /**
     Shared Preference Keys(prefSettings):
     1 - imageQuality
     2 - detailImageQuality
     3 - nightMode
     4 - enableAnimations
     5 - enableDynamicColoring
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* When activity starts before onCreate method call */
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);
        boolean boolValMode = mSharedPreferences.getBoolean("nightMode", false);
        if(!boolValMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);


        /* ---------------- For Image Quality  --------   */
        mBinding.settingsImageSpinner1.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_settings_activity, imageQuality);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.settingsImageSpinner1.setAdapter(adapter);

        String spinVal = mSharedPreferences.getString("imageQuality", "Medium");
        for(int i=0; i<imageQuality.length; i++) {
            if(spinVal != null && spinVal.equals(mBinding.settingsImageSpinner1.getItemAtPosition(i).toString())) {
                mBinding.settingsImageSpinner1.setSelection(i);
                break;
            }
        }

        mBinding.settingsImageSpinner2.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.list_item_settings_activity, imageQuality);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.settingsImageSpinner2.setAdapter(adapter2);

        String spinVal2 = mSharedPreferences.getString("detailImageQuality", "Medium");
        for(int i=0; i<imageQuality.length; i++) {
            if(spinVal != null && spinVal2.equals(mBinding.settingsImageSpinner2.getItemAtPosition(i).toString())) {
                mBinding.settingsImageSpinner2.setSelection(i);
                break;
            }
        }


        /* ------------------------------- For Night Mode Selection ----------------------------   */
        mBinding.settingsModeSwitch.setChecked(boolValMode);
        mBinding.settingsModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                if(isChecked) {
                    editor.putBoolean("nightMode", true);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    mBinding.settingsModeSwitch.setChecked(true);
                }
                else {
                    editor.putBoolean("nightMode", false);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    mBinding.settingsModeSwitch.setChecked(false);
                }
            }
        });

        /* ------------------------------ Search History Delete ------------------------------- */
        mBinding.settingsCv2RlHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistoryDeleteBuilder();
            }
        });

        /* ------------------------------- For Animation  ----------------------------------   */
        boolean isCheckedAnimation;
        isCheckedAnimation = mSharedPreferences.getBoolean("enableAnimations", true);

        mBinding.settingsAnimationSwitch.setChecked(isCheckedAnimation);
        mBinding.settingsAnimationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                if(isChecked) {
                    editor.putBoolean("enableAnimations", true);
                    editor.apply();

                    mBinding.settingsAnimationSwitch.setChecked(true);
                }
                else {
                    editor.putBoolean("enableAnimations", false);
                    editor.apply();

                    mBinding.settingsAnimationSwitch.setChecked(false);
                }
            }
        });

        /* ---------------------------- Dynamic Coloring ------------------------------------------- */
        boolean isCheckedColor;
        isCheckedColor = mSharedPreferences.getBoolean("enableDynamicColoring", true);

        mBinding.settingsColoringSwitch.setChecked(isCheckedColor);
        mBinding.settingsColoringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                if(isChecked) {
                    editor.putBoolean("enableDynamicColoring", true);
                    editor.apply();

                    mBinding.settingsColoringSwitch.setChecked(true);
                }
                else {
                    editor.putBoolean("enableDynamicColoring", false);
                    editor.apply();

                    mBinding.settingsColoringSwitch.setChecked(false);
                }
            }
        });

        getSupportActionBar().setTitle("Settings");
    }

    private void showHistoryDeleteBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you wish to delete search history?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = getSharedPreferences("listPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedString1 = mBinding.settingsImageSpinner1.getSelectedItem().toString();
        SharedPreferences.Editor editor1 = mSharedPreferences.edit();
        editor1.putString("imageQuality", selectedString1);
        editor1.apply();

        String selectedString2 = mBinding.settingsImageSpinner2.getSelectedItem().toString();
        SharedPreferences.Editor editor2 = mSharedPreferences.edit();
        editor2.putString("detailImageQuality", selectedString2);
        editor2.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);
        if(mSharedPreferences.getBoolean("enableAnimations", true)) {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}