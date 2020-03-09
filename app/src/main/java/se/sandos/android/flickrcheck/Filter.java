package se.sandos.android.flickrcheck;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import se.sandos.flickrcheck.R;

public class Filter extends Activity {

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        mPrefs = this.getPreferences(0);
        if(mPrefs.contains("FILTER_UNIT") && mPrefs.contains("FILTER_LEN"))
        {
            EditText et = (EditText) findViewById(android.R.id.content).getRootView().findViewById(R.id.filterLen);
            et.setText(mPrefs.getString("FILTER_LEN", "6"));
        }
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        EditText et = (EditText) findViewById(android.R.id.content).getRootView().findViewById(R.id.filterLen);
        ed.putString("FILTER_LEN", et.getText().toString());

        String unit = "Months";
        RadioGroup grp = (RadioGroup) findViewById(android.R.id.content).getRootView().findViewById(R.id.unitgroup);

        switch(grp.getCheckedRadioButtonId()){
            case R.id.radioButtonDays: unit = "Days"; break;
            case R.id.radioButtonYears: unit = "Years"; break;
            case R.id.radioButtonWeeks: unit = "Weeks"; break;
        }
        ed.putString("FILTER_UNIT", unit);
        ed.commit();
    }
}
