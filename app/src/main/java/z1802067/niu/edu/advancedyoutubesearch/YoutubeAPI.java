package z1802067.niu.edu.advancedyoutubesearch;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class YoutubeAPI {
    private static final String TAG = "YoutubeAPI";
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
    private static YouTube youtube;
    private static String API_KEY = "AIzaSyDZbQufbP8TAcRPBpsFSCKG1aYckDQfSbI";
    private static List<SearchResult>[] results = new List[1];
    private static List<Video>[] vidResults = new List[1];


    /*
    ytsearch will initialize youtube object and search youtube with given parameters
     */
    public static List<SearchResult> ytSearch(String query,String orderby,String dateAfter,String dateBefore, String safeSearch, String length,String definition){
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("Advanced Youtube Search").build();
        try {
            final YouTube.Search.List search = youtube.search().list("id");

            //set parameters
            search.setKey(API_KEY);
            search.setQ(query);
            search.setType("video");
            search.setOrder(getOrderByString(orderby));
            search.setSafeSearch(safeSearch);
            search.setVideoDuration(length);
            search.setVideoDefinition(definition);

            //if given dates have values then set appropriate search parameters
            if(dateAfter != null) {
                if (dateAfter.length() > 0) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/dd/yyyy");
                    Date date = simpleDateFormat.parse(dateAfter);
                    //Log.d(TAG, "ytSearch: " + date.toString());
                    DateTime dateTime = new DateTime(date);
                    search.setPublishedAfter(dateTime);
                }
            }

            if(dateBefore != null) {
                if (dateBefore.length() > 0) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/dd/yyyy");
                    Date date = simpleDateFormat.parse(dateBefore);
                    //Log.d(TAG, "ytSearch: " + date.toString());
                    DateTime dateTime = new DateTime(date);
                    search.setPublishedBefore(dateTime);
                }
            }


            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            //create a latch to wait for new thread to finish work
            final CountDownLatch latch = new CountDownLatch(1);
            //new thread as network operations cant be done on main thread
            new Thread(){
                public void run(){
                    SearchListResponse searchResponse = null;
                    try {
                        //execute search and wait to finish
                        searchResponse = search.execute();
                        results[0] = searchResponse.getItems();
                        latch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            latch.await();
            //return search results
            return results[0];
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    getVidid adds all video ids from a searchresult list to a single string so that it can be used
    to get a list of videos
     */
    public static String getVidID(List<SearchResult> videos){
        List<String> videoIds = new ArrayList<String>();
        for(SearchResult searchResult : videos){
            videoIds.add(searchResult.getId().getVideoId());
        }
        Joiner stringJoiner = Joiner.on(',');

        return stringJoiner.join(videoIds);
    }

    /*
    getViewsLikes takes a string of joined ids and gets a list of videos from youtube api
     */
    public static List<Video> getViewsLikes(String ids){
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("Advanced Youtube Search").build();
        try{
            Log.d(TAG, ids);
            //create new video search and set neccessary properties
            final YouTube.Videos.List videoSearch = youtube.videos().list("statistics,snippet").setId(ids);
            videoSearch.setKey(API_KEY);
            //videoSearch.setId(ids);
            videoSearch.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            //new thread and latch for network
            final CountDownLatch latch = new CountDownLatch(1);
            new Thread(){
                public void run(){
                    VideoListResponse listResponse = null;
                    try {
                        //execute search
                        listResponse = videoSearch.execute();
                        vidResults[0] = listResponse.getItems();
                        latch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            latch.await();
            //return results
            return vidResults[0];
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //convert given order by string to a valid string for search
    public static String getOrderByString(String orderBy){
        switch (orderBy){
            case "Relevance":
                return "relevance";
            case "Views":
                return "viewCount";
            case "Rating":
                return "rating";
            case "Date":
                return "date";
            case "Title":
                return "title";
        }
        return null;
    }
}


