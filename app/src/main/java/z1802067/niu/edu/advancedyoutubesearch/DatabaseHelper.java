package z1802067.niu.edu.advancedyoutubesearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

import static z1802067.niu.edu.advancedyoutubesearch.YoutubeAPI.getViewsLikes;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "watchlist.db";
    public static final String TABLE_NAME = "video_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "VIDEO_ID";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, VIDEO_ID varchar(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String videoID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO video_table (VIDEO_ID) " +
                "VALUES ('" + videoID + "')");
    }

    public void delete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE VIDEO_ID='" + id +"'");
    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE video_table");
    }

    public String getVideos(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("select * from video_table",null);
        ArrayList<String> vidIDs = new ArrayList<String>();
        if(results.getCount() > 0){
            results.moveToFirst();
            vidIDs.add(results.getString(1));
            while(results.moveToNext()){
                vidIDs.add(results.getString(1));
            }
            Joiner stringJoiner = Joiner.on(',');
            String ids = stringJoiner.join(vidIDs);
            Log.d(TAG, ids);
            return ids;
        }
        return null;
    }
}
