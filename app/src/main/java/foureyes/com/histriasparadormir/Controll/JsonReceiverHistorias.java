package foureyes.com.histriasparadormir.Controll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import foureyes.com.histriasparadormir.DAO.Banco;
import foureyes.com.histriasparadormir.Model.Historia;
import foureyes.com.histriasparadormir.View.Exibe_Lista;
import foureyes.com.histriasparadormir.View.MainActivity;

/**
 * Created by dev on 21/02/18.
 */

public class JsonReceiverHistorias extends AsyncTask<String, Void, Void> {

    private static Context context;
    private WeakReference<MainActivity> activityReference;
    private String conteudo = null, erro = null;
    ProgressDialog dialog;
    private ArrayList<Historia> lHistorias = new ArrayList<>();
    private Banco banco;

    public JsonReceiverHistorias(Context context) {
        this.context = context;
        this.activityReference = new WeakReference<MainActivity>((MainActivity) context);
        dialog = ProgressDialog.show(context, "", "Aguarde, baixando histórias...", true);
        banco = new Banco(context, null, null, 1);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date updated = new Date();
        BufferedReader reader = null;

        try {
            //Url do webservice
            URL url = new URL(params[0]);

            //Envia a requisicao POST
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            //Recebe a resposta do servidor
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Le a resposta do servidor
            while ((line = reader.readLine()) != null) {
                //Coloca a resposta do servidor e uma string
                sb.append(line + "");
            }

            conteudo = sb.toString();
        } catch (Exception ex) {
            erro = ex.getMessage();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }


        if (erro != null) {
            Toast.makeText(context, "Erro: " + erro, Toast.LENGTH_SHORT).show();
        } else {

            JSONObject respostaJson = null;
            String id, titulo, category = null, thumbnail, content;

            try {

                //Cria um novo Jsonobjeto com nome/valor mapeado do json
                respostaJson = new JSONObject(conteudo);


                //Resorna o valor mapeado pelo nome se existir
                JSONArray jsonNoPrincipal = respostaJson.optJSONArray("posts");

                for (int i = 0; i < jsonNoPrincipal.length(); i++) {
                    //Pega o objeto de cada no JSON
                    JSONObject jsonNoFilho = jsonNoPrincipal.getJSONObject(i);

                    id = jsonNoFilho.getString("id");
                    titulo = jsonNoFilho.getString("title");
                    content = jsonNoFilho.getString("content");
                    thumbnail = jsonNoFilho.getString("thumbnail");


                    String thumbFile = baixaESalvaImg(thumbnail, id);

                    //Pega o no de categorias
                    JSONArray categories = jsonNoFilho.getJSONArray("categories");

                    //Varre o array de categorias e pega o objeto de nome title
                    for (int n = 0; n < categories.length(); n++) {
                        JSONObject jsonCategories = categories.getJSONObject(n);
                        category = jsonCategories.getString("title");
                    }

                    StringBuffer categoria = new StringBuffer();
                    categoria.append(category);

                    Historia l = new Historia(titulo, content, String.valueOf(categoria), thumbFile);
                    lHistorias.add(l);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            banco.resetaBanco();
            banco.atualizaBanco(lHistorias);
            banco.setLastUpdate(String.valueOf(dateFormat.format(updated)));
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
        context.startActivity(new Intent(context, Exibe_Lista.class));
    }

    public String baixaESalvaImg(String thumbnail, String idPost) {
        String imgName;
        imgName = thumbnail.substring(thumbnail.lastIndexOf('/') + 1);
        URL bit;
        Bitmap bmp = null;

        try {
            bit = new URL(thumbnail);
            bmp = BitmapFactory.decodeStream(bit.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File myDir = new File(String.valueOf(context.getExternalFilesDir(null)));
        myDir.mkdirs();
        File file = new File(myDir, idPost + "_" + imgName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(file);
    }
}
