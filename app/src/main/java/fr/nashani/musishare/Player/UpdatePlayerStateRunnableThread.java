package fr.nashani.musishare.Player;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdatePlayerStateRunnableThread implements Runnable {

    private Music music;
    private Handler handlerUI;
    private FirebaseAuth mAuth;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;

    public UpdatePlayerStateRunnableThread(Music music, Handler handlerUI, String mAccessToken) {
        this.music = music;
        this.handlerUI = handlerUI;
        this.mAccessToken = mAccessToken;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void run() {
        getMusicData();
        handlerUI.postDelayed(this,5000);
    }

    private void getMusicData() {
        if (mAccessToken == null) {
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player")
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    final JSONObject items = (JSONObject) jsonObject.get("item");

                    //getTrackName
                    String trackName = items.get("name").toString();

                    //getTrackArtists
                    List<String> trackArtists = new ArrayList<>();
                    JSONArray artistsArray = items.getJSONArray("artists");

                    for(int i=0;i<artistsArray.length();i++){
                        JSONObject artist = artistsArray.getJSONObject(i);
                        trackArtists.add(artist.getString("name"));
                    }

                    final JSONObject album = (JSONObject) items.get("album");
                    //getTrackAlbum
                    String trackAlbumName = album.getString("name");


                    //getAlbumCoverName
                    JSONArray images = album.getJSONArray("images");
                    String trackAlbumCover = images.getJSONObject(1).getString("url");

                    if()
                    saveTrack(trackName,trackAlbumName,trackAlbumCover,trackArtists);

                } catch (JSONException e) {

                }
            }
        });
    }

    private void saveTrack(String trackName, String trackAlbumName, String trackAlbumCoverURL, List<String> trackArtists){

        String userId = mAuth.getCurrentUser().getUid() ;

        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("LastPlayedTrack");

        Map<String, Object> LastPlayedTrackInformation = new HashMap<>();

        LastPlayedTrackInformation.put("trackName",trackName);
        LastPlayedTrackInformation.put("trackAlbum",trackAlbumName);
        LastPlayedTrackInformation.put("trackAlbumCoverURL",trackAlbumCoverURL);

        currentUserDB.setValue(LastPlayedTrackInformation);

        currentUserDB.updateChildren(LastPlayedTrackInformation);

        //artists
        DatabaseReference currentUserDBArtists = currentUserDB.child("artists");

        Map<String, Object> artists = new HashMap<>();

        int i = 0;
        for (String artist : trackArtists){
            artists.put(""+i,artist);
            i++;
        }

        currentUserDBArtists.setValue(artists);

        currentUserDBArtists.updateChildren(artists);

    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

}
