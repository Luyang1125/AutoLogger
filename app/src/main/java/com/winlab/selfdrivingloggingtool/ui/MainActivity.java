package com.winlab.selfdrivingloggingtool.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.winlab.selfdrivingloggingtool.R;

public class MainActivity extends AppCompatActivity{

    private static Thread mThread;
    private MainFragment firstFragment;

    @Override
    protected void onPause() {
        super.onPause();
//        mRecorder.pause();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            firstFragment = new MainFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingsFragment fragment = new SettingsFragment();


            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();;

            return true;
        }else if(id ==R.id.action_startCamera){
            Log.i("MainActivity", "Starting Video Recording");
            firstFragment.startLogging();
        }else if(id ==R.id.action_stopCamera) {
            Log.i("MainActivity","Stoping Video Recording");
            firstFragment.stopLogging();
        }
        return super.onOptionsItemSelected(item);
    }




}
