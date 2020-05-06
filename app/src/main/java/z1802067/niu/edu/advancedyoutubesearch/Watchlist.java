package z1802067.niu.edu.advancedyoutubesearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.services.youtube.model.Video;

import java.util.List;

/*
watchlist contains a recyclerview for list of saved videos
calls sqlite database to get saved video ids
 */
public class Watchlist extends AppCompatActivity {
    private static final String TAG = "Watchlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        //init recyclerview and back button
        initRecyclerView();
        initBackButton();
    }

    public void initRecyclerView(){
        RecyclerView watchlist = (RecyclerView)findViewById(R.id.watchList);
        DatabaseHelper db = new DatabaseHelper(this);
        //get video ids from datbase and call youtube api with those ids
        String vids = db.getVideos();

        if(vids != null) {//if there are videos in list
            List<Video> videos = YoutubeAPI.getViewsLikes(vids);
            Log.d(TAG, videos.get(0).getSnippet().getTitle().toString());
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(videos,this);
            watchlist.setAdapter(recyclerViewAdapter);
            watchlist.setLayoutManager(new LinearLayoutManager(this));
        }
        else{
            Toast toast = Toast.makeText(this,"You currently have no videos in your watchlist.",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //starts main activity
    public void initBackButton(){
        Button backButton = (Button)findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Watchlist.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
