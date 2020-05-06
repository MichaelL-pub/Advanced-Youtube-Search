package z1802067.niu.edu.advancedyoutubesearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;


/*
searchresults class calls youtube api with extras from mainactivity and then initializes
recycler view with the results
 */
public class SearchResults extends AppCompatActivity {
    private static final String TAG = "SearchResults";
    private ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        //hide top title bar
        getSupportActionBar().hide();
        //initialize recycler view and back button
        backButtonClick();
        initRecyclerView();

    }

    /*
    initrecyclerview calls youtube Search.list with given parameter extras, then passes list
    to YoutubeApi.getviewslikes and passes the list of videos to a custom recyclerviewAdapter
     */
    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.videoRView);
        //get extras from mainactivity
        String query = getIntent().getStringExtra("query");
        String orderBy = getIntent().getStringExtra("orderby");
        String dateAfter = getIntent().getStringExtra("dateAfter");
        String dateBefore = getIntent().getStringExtra("dateBefore");
        String safeSearch = getIntent().getStringExtra("safeSearch");
        String length = getIntent().getStringExtra("length");
        String definition = getIntent().getStringExtra("definition");
        Log.d(TAG, "new search - query: " + query + " order by: " + orderBy
        + " after date: " + dateAfter + " before date: " + dateBefore + " safe search: " + safeSearch
        + " length: " + length + " definition: " + definition);
        //call ytsearch with given extras
        List<SearchResult> results = YoutubeAPI.ytSearch(query,orderBy,dateAfter,dateBefore,safeSearch,length,definition);
        //pass result to getviews likes for a list of videos
        List<Video> videos = YoutubeAPI.getViewsLikes(YoutubeAPI.getVidID(results));
        //pass videos to recyclerviewadapter to initialize recycler view with videos
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(videos,this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //back button will start main activity
    private void backButtonClick(){
        Button backButton =(Button)findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResults.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
