package com.michael.flickrbrowser.flick;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 1/5/2017.
 */

public class GetFlickrJsonData extends GetRawData {

    private String LOG_TAG = GetFlickrJsonData.class.getSimpleName();
    private List<Photo> mPhotos;
    private Uri mDestinationUri;

    public GetFlickrJsonData(String searchCriteria, boolean matchAll) {
        super(null);
        createAndUpdateUri(searchCriteria,matchAll);
        mPhotos = new ArrayList<Photo>();
    }

    public void execute() {
        super.setmRawUrl(mDestinationUri.toString());
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.v(LOG_TAG, "Built URI = " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String searchCriteria, boolean matchAll) {
        final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAGMODE_PARAM = "tagmode";
        final String FORMAT_PARAM = "format";
        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";

        mDestinationUri = Uri.parse(FLICKR_API_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM,searchCriteria)
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();

        return mDestinationUri != null;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public void processResult() {

        if(getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw file");
            return;
        }

        final String FLICKR_ITEMS = "items";
        final String FLICKR_TITLE = "title";
        final String FLICKR_MEDIA = "media";
        final String FLICKR_PHOTO_URL = "m";
        final String FLICKR_AUTHOR = "author";
        final String FLICKR_AUTHOR_ID = "author_id";
        final String FLICKR_LINK = "link";
        final String FLICKR_TAGS = "tags";

        try {

            JSONObject jsonData = new JSONObject(getmData());
            JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS);
            for(int i=0; i<itemsArray.length(); i++) {

                JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                String title = jsonPhoto.getString(FLICKR_TITLE);
                String author = jsonPhoto.getString(FLICKR_AUTHOR);
                String authorId = jsonPhoto.getString(FLICKR_AUTHOR_ID);
                //String link = jsonPhoto.getString(FLICKR_LINK);
                String tags = jsonPhoto.getString(FLICKR_TAGS);

                JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA);
                String photoUrl = jsonMedia.getString(FLICKR_PHOTO_URL);
                String link = photoUrl.replaceFirst("_m.","_b.");
                Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);

                this.mPhotos.add(photoObject);
            }

            for(Photo singlePhoto: mPhotos) {
                Log.v(LOG_TAG, singlePhoto.toString());
            }

        } catch(JSONException jsone) {
            jsone.printStackTrace();
            Log.e(LOG_TAG, "Error processing Json data");
        }

    }

    public class DownloadJsonData extends DownloadRawData {

        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();

        }

        protected String doInBackground(String... params) {
            String[] par = { mDestinationUri.toString()};
            return super.doInBackground(par);
        }

    }

}

//public class GetFlickrJsonData extends GetRawData {
//
//    private String LOG_TAG = GetFlickrJsonData.class.getSimpleName();
//    private List<Photo> mPhotos;
//    private Uri mDestinationUri;
//
//    public GetFlickrJsonData(String searchCriteria, boolean matchAll){
//        //matchAll if matchall set to on, tagmode will set to all , if matchall set to off we will search for any
//        super(null);
//        createAndUpdateUri(searchCriteria, matchAll);
//        mPhotos = new ArrayList<Photo>();
//    }
//
//    public void exercute(){
//        super.setmRawUrl(mDestinationUri.toString());
//        DownloadJsonData downloadJsonData = new DownloadJsonData();
//        Log.v(LOG_TAG, "Built URI = " + mDestinationUri.toString());
//        downloadJsonData.execute(mDestinationUri.toString());
//    }
//
//    //build up the URI
//    public boolean createAndUpdateUri(String searchCriteria, boolean matchAll){
//        final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
//        final String TAGS_PARAM = "tags";
//        final String TAGMODE_PARAM = "tagmode";
//        final String  FORMAT_PARAM = "format";
//        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";
//
//        mDestinationUri =
//                Uri.parse(FLICKR_API_BASE_URL).buildUpon()
//                        .appendQueryParameter(TAGS_PARAM,searchCriteria)
//                        .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
//                        .appendQueryParameter(FORMAT_PARAM, "json")
//                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
//                .build();
//        return mDestinationUri !=null; //return true of it is not null and return false if it is null
//    }
//
//    public void processResult(){
//        if(getmDownloadStatus() != DownloadStatus.OK){
//            Log.e(LOG_TAG, "Error downloading raw file");
//            return;
//        }
////        {
////            "title": "Retrato com c\u00e2mera traseira",
////                "link": "https:\/\/www.flickr.com\/photos\/eutestei\/28366106666\/",
////                "media": {"m":"https:\/\/farm9.staticflickr.com\/8760\/28366106666_39502be4a7_m.jpg"},
////            "date_taken": "2015-02-05T16:52:13-08:00",
////                "description": " <p><a href=\"https:\/\/www.flickr.com\/people\/eutestei\/\">eutestei<\/a> posted a photo:<\/p> <p><a href=\"https:\/\/www.flickr.com\/photos\/eutestei\/28366106666\/\" title=\"Retrato com c\u00e2mera traseira\"><img src=\"https:\/\/farm9.staticflickr.com\/8760\/28366106666_39502be4a7_m.jpg\" width=\"240\" height=\"135\" alt=\"Retrato com c\u00e2mera traseira\" \/><\/a><\/p> <p>Feita com o smartphone Samsung Galaxy A5 2016<\/p>",
////                "published": "2016-07-18T23:11:32Z",
////                "author": "nobody@flickr.com (\"eutestei\")",
////                "author_id": "133049776@N08",
////                "tags": "brasil review smartphone celular gadget lollipop teste android tecnologia mobilidade m\u00f3vel an\u00e1lise resenha octacore touchwiz eutestei stelladauer v\u00eddeocompleto samsunga5 resenhacompleta galaxya52016 galaxya5\u00e9bom testedogalaxya52016 an\u00e1lisedogalaxya52016 sma510mds sma510m a510m galaxya5vaiatualizarparamarshmallow chipexynos celularpremiumdasamsung highrangesamsung"
////        },
//
//
//
//        final String FLICKR_ITEMS = "items";
//        final String FLICKR_TITLE = "title";
//        final String FLICKR_MEDIA = "media";
//        final String FLICKR_PHOTO_URL = "m";
//        final String FLICKR_AUTHOR = "author";
//        final String FLICKR_AUTHOR_ID = "author_id";
//        final String FLICKR_LINK = "link";
//        final String FLICKR_TAGS = "tags";
//
//
//
//        try{
//            JSONObject jsonData = new JSONObject(getmData());
//            JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS); //items array has all the items load started at items [ till the end ] of the file
//
//            for(int i = 0 ; i<itemsArray.length(); i++){
//                JSONObject jsonPhoto = itemsArray.getJSONObject(i);
//                String title = jsonPhoto.getString(FLICKR_TITLE);//get the title string
//                String author = jsonPhoto.getString(FLICKR_AUTHOR);
//                String authorId = jsonPhoto.getString(FLICKR_AUTHOR_ID);
//                String link = jsonPhoto.getString(FLICKR_LINK);
//                String tags = jsonPhoto.getString(FLICKR_TAGS);
//
//                JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA); //load the whole {"m":"https:\/\/farm9.staticflickr.com\/8760\/28366106666_39502be4a7_m.jpg"},
//                String photoUrl = jsonMedia.getString(FLICKR_PHOTO_URL); //then load https:\/\/farm9.staticflickr.com\/8760\/28366106666_39502be4a7_m.jpg
//
//                Photo photoObject = new Photo(title,author,authorId,link,tags,photoUrl); //create a photo object
//                this.mPhotos.add(photoObject); //add the above photo to the mphotos list
//
//
//            }
//            //print the photo list out using overide toString method in Photo CLass
//
//            for(Photo singlePhoto: mPhotos){
//                Log.v(LOG_TAG, singlePhoto.toString());
//            }
//
//        }catch(JSONException jsone){
//            jsone.printStackTrace();
//            Log.e(LOG_TAG,"error processing Json data");
//        }
//
//
//    }
//
//    public class DownloadJsonData extends  DownloadRawData{
//        protected void onPostExecute(String webData) {
//            super.onPostExecute(webData);
//            processResult();
//        }
//
//        protected String doInBackGround(String... params){
//            return super.doInBackground(params);
//        }
//    }
//
//
//}
