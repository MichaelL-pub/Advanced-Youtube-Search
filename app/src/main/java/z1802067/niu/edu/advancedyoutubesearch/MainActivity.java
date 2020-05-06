/*
Michael Leszczynski
Z1802067
CSCI 428 final project
this program will search youtube using the Youtube data v3 API
 with parameters given by the user and allows for the saving of videos
in a sqlite database for a watchlist which can be viewed later
 */

package z1802067.niu.edu.advancedyoutubesearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;

import java.util.Calendar;
import java.util.List;

/*
Mainactivity has the search parameter inputs and button to go to watchlist
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the code for watchlist click
        initWatchlistButton();

        //init spinners for order by, safesearch, video length and definition parameters
        final Spinner orderSpinner = (Spinner)findViewById(R.id.orderBySpinner);
        String[] orderOptions = new String[] {"Relevance","Views","Rating","Date","Title"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, orderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapter);

        final Spinner safeSearchSpinner = (Spinner)findViewById(R.id.safeSearchSpinner);
        String[] safeSearchOptions = new String[] {"none","moderate","strict"};
        ArrayAdapter<String> ssAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, safeSearchOptions);
        ssAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        safeSearchSpinner.setAdapter(ssAdapter);

        final Spinner lengthSpinner = (Spinner)findViewById(R.id.lengthSpinner);
        String[] lengthOptions = new String[]{"any","short","medium","long"};
        ArrayAdapter<String> lengthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,lengthOptions);
        lengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lengthSpinner.setAdapter(lengthAdapter);

        final Spinner defSpinner = (Spinner)findViewById(R.id.definitionSpinner);
        String[] definitionOptions = new String[]{"any","standard","high"};
        ArrayAdapter<String> definitionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,definitionOptions);
        definitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defSpinner.setAdapter(definitionAdapter);

        final EditText dateAfter = (EditText)findViewById(R.id.dateAfter);
        final EditText dateBefore = (EditText)findViewById(R.id.dateBefore);
        Button searchButton = (Button) findViewById(R.id.searchButton);

        //when search button is clicked
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchBar = (EditText) findViewById(R.id.searchBar);
                //init intent and put parameters that have default values as extras
                Intent intent = new Intent(MainActivity.this, SearchResults.class);
                intent.putExtra("query",searchBar.getText().toString());
                intent.putExtra("orderby",orderSpinner.getSelectedItem().toString());
                intent.putExtra("safeSearch",safeSearchSpinner.getSelectedItem().toString());
                intent.putExtra("length",lengthSpinner.getSelectedItem().toString());
                intent.putExtra("definition",defSpinner.getSelectedItem().toString());

                boolean afterCheck = false;
                boolean beforeCheck = false;

                //if published after doesn't a value send it as empty string
                if(dateAfter.getText().toString().length() <= 0){
                    intent.putExtra("dateAfter","");
                    afterCheck = true;
                }
                else {//published after has a value
                    if(checkDate(dateAfter.getText().toString())) {//check if given string is a realistic date
                        //put string as extra if its valid
                        intent.putExtra("dateAfter", dateAfter.getText().toString());
                        afterCheck = true;
                    }
                    else{
                        //show toast to let user know date input wasnt valid
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter a valid date such as 01/01/2010", Toast.LENGTH_LONG);
                        toast.show(); }
                }

                //if published before doesn't a value send it as empty string
                if(dateBefore.getText().toString().length() <= 0) {
                    intent.putExtra("dateBefore", "");
                    beforeCheck = true;
                }
                else {//published before has a value
                    if(checkDate(dateBefore.getText().toString())) {//check if given string is a realistic date
                        intent.putExtra("dateBefore", dateBefore.getText().toString());
                        beforeCheck = true;}
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter a valid date such as 01/01/2010", Toast.LENGTH_LONG);
                        toast.show(); }
                }
                //start searchresult activity
                if(afterCheck && beforeCheck){
                    startActivity(intent);
                }
            }
        });
    }

    /*
    checkdate method checks if date input is in proper format and is relativly valid
    does not check that a date actually exists but should work for most dates
     */
    public boolean checkDate(String date) {
        //MM/DD/YYYY
        //if date has a value and isnt null
         if(date.length()>0 && date != null){
         if (date.charAt(2) == '/' && date.charAt(5) == '/') {// backslash in proper position
               if (date.length() == 10) {//if proper length
                    String[] dates = date.split("/");
                    //if the input contains digit in proper locations
                    if (dates[0].matches("\\d\\d") && dates[1].matches("\\d\\d") && dates[2].matches("\\d\\d\\d\\d")) {
                        int day = Integer.parseInt(dates[1]);
                        int month = Integer.parseInt(dates[0]);
                        int year = Integer.parseInt(dates[2]);
                        //if date month year are possible values while
                        if (day < 32 && month < 13 && year <= Calendar.getInstance().get(Calendar.YEAR) && year > 2004) {
                            return true;
                        }
                    }
                }
            }
        }
         //value did not make it through the if block gauntlet
        return false;
    }

    /*
    initwatchlistbutton will init and intent and start watchlist activity
     */
    public void initWatchlistButton(){
        Button watchlistButton = (Button)findViewById(R.id.watchlistButton);
        watchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Watchlist.class);
                startActivity(intent);
            }
        });
    }
 }







