package com.example.projet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GenreActivity extends AppCompatActivity {

    ListView genreListView;
    ArrayList<String> genreList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        genreListView = findViewById(R.id.genreListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genreList);
        genreListView.setAdapter(adapter);

        new FetchGenresTask().execute();

        genreListView.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedGenre = genreList.get(position);
            Intent intent = new Intent(GenreActivity.this, ArtistActivity.class);
            intent.putExtra("genre", selectedGenre);
            startActivity(intent);
        });
    }

    class FetchGenresTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> result = new ArrayList<>();
            try {
                URL url = new URL("http://10.0.2.2:8000/genres");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    response.append(line);
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    result.add(jsonArray.getString(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> genres) {
            genreList.clear();
            genreList.addAll(genres);
            adapter.notifyDataSetChanged();
        }
    }
}