package foureyes.com.histriasparadormir.Controll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

import foureyes.com.histriasparadormir.DAO.Database;
import foureyes.com.histriasparadormir.Model.Story;
import foureyes.com.histriasparadormir.R;
import foureyes.com.histriasparadormir.View.StoryList;
import foureyes.com.histriasparadormir.View.MainActivity;

/**
 * Created by dev on 21/02/18.
 */

public class JsonReceiverHistorias extends AsyncTask<String, Void, Void> {

    private static Context context;
    private final WeakReference<MainActivity> activityReference;
    private String content = null, error = null;
    ProgressDialog dialog;
    private final ArrayList<Story> lStories = new ArrayList<>();
    private final Database database;

    public JsonReceiverHistorias(Context context) {
        JsonReceiverHistorias.context = context;
        this.activityReference = new WeakReference<MainActivity>((MainActivity) context);
        dialog = ProgressDialog.show(context, "", context.getString(R.string.downloading_message), true);
        database = new Database(context, null, null, 1);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        BufferedReader reader = null;

        try {
            // API URL
            URL url = new URL(params[0]);

            // Create connection and make POST request
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);

            // Read response
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // Append response to stringBuilder
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            content = stringBuilder.toString();
        } catch (Exception ex) {
            error = ex.getMessage();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (Exception ignored) {
            }
        }


        if (error != null) {
            Toast.makeText(context, R.string.error + error, Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jsonObject;
            String id, title, category = null, thumbnailUri, thumbnailFile = null, content;

            try {
                // Create a new JSON object with name/value pair
                jsonObject = new JSONObject(this.content);

                // Get the main array key "posts"
                JSONArray postsArray = jsonObject.optJSONArray("posts");

                for (int i = 0; i < Objects.requireNonNull(postsArray).length(); i++) {
                    JSONObject postObject = postsArray.getJSONObject(i);

                    id = postObject.getString("id");
                    title = postObject.getString("title");
                    content = postObject.getString("content");
                    thumbnailUri = postObject.getString("thumbnail");
                    thumbnailFile = downloadThumbnail(thumbnailUri, id);

                    JSONArray categories = postObject.getJSONArray("categories");
                    for (int n = 0; n < categories.length(); n++) {
                        JSONObject jsonCategories = categories.getJSONObject(n);
                        category = jsonCategories.getString("title");
                    }

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(category);
                    Story story = new Story(title, content, String.valueOf(stringBuffer), thumbnailFile);
                    lStories.add(story);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            database.resetDatabase();
            database.updateDatabase(lStories);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        MainActivity activity = activityReference.get();
        if (activity.isDestroyed()) { // or call isFinishing() if min sdk version < 17
            return;
        }
        if (!activity.isFinishing() && dialog != null) {
            dialog.dismiss();
        }
        context.startActivity(new Intent(context, StoryList.class));
    }

    public String downloadThumbnail(String thumbnailUri, String postId) {
        String imgName = thumbnailUri.substring(thumbnailUri.lastIndexOf('/') + 1);
        URL url;
        Bitmap bitmap = null;

        try {
            url = new URL(thumbnailUri);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        File myDir = new File(String.valueOf(context.getExternalFilesDir(null)));
        myDir.mkdirs();
        File file = new File(myDir, postId + "_" + imgName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(file);
    }
}
