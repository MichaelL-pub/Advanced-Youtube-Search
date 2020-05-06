package z1802067.niu.edu.advancedyoutubesearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/*
recyclerviewadapter class contains functionalit for the recycler views that contain videos
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<Video> videos;
    private Context context;
    private static final String TAG = "RecyclerViewAdapter";


    //constructor
    public RecyclerViewAdapter(List<Video> videos, Context context) {
        this.videos = videos;
        //this.images = images;
        this.context = context;
    }

    //format views and likes
    private String formatNumber(String num){
        Double number = Double.parseDouble(num);
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(true);
        return format.format(number);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    //when an item is shown in the view
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Video curVideo = videos.get(position);
        //get needed thumbnail image url
        String url = curVideo.getSnippet().getThumbnails().getHigh().getUrl();
        Log.d(TAG, context.toString());

        //set imageview icon to url
        new DownloadImageTask((ImageView) holder.thumbnail).execute(url);
        Log.d(TAG, "onBindViewHolder: " + url);
        //set video components to their values
        holder.title.setText(curVideo.getSnippet().getTitle());
        holder.chanel.setText(curVideo.getSnippet().getChannelTitle());
        String views = formatNumber(curVideo.getStatistics().getViewCount().toString());
        String likes = formatNumber(curVideo.getStatistics().getLikeCount().toString());
        holder.views.setText(views + " Views");
        holder.likes.setText(likes + " Likes");

        //when video image clicked go open video link
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+curVideo.getId());
                String url = "http://youtube.com/watch?v="+ curVideo.getId();
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url)));
            }
        });

        //add to playlist is clicked
        if(context.toString().contains("SearchResults")) {//if recycler view is in search results
            //init add to playlist button
            holder.playlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //date videoid to database
                    DatabaseHelper db = new DatabaseHelper(context);
                    db.insert(curVideo.getId().toString());
                }
            });
        }
        else{
            //init remove from watchlist button
            holder.playlistButton.setText("Remove From watchlist");
            holder.playlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper db = new DatabaseHelper(context);
                    //delete video id from table
                    db.delete(curVideo.getId().toString());
                    Toast toast = Toast.makeText(context,"Please reload this page for changes to take effect",Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView chanel;
        TextView views;
        TextView likes;
        ImageView thumbnail;
        RelativeLayout video_layout;
        Button playlistButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            video_layout = itemView.findViewById(R.id.video_layout);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            chanel = itemView.findViewById(R.id.chanel);
            views = itemView.findViewById(R.id.views);
            likes = itemView.findViewById(R.id.likes);
            playlistButton = itemView.findViewById(R.id.playlistButton);

        }
    }


    //downloadImageTask is used to get an image from a url
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
