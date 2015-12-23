package com.example.lucindalim.guideapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String URL = "https://guidebook.com/service/v2/upcomingGuides/";
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = (ListView) findViewById(R.id.listView);
        new AsyncTask<Void, Void, CustomList>() {
            @Override
            protected CustomList doInBackground(Void... param) {
                try {
                    JSONArray jsonArray = readJsonFromUrl(URL).getJSONArray("data");
                    ArrayList<JSONObject> jsonObjectsList = new ArrayList<JSONObject>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObjectsList.add(jsonArray.getJSONObject(i));

                    }

                    CustomList listAdapter = new CustomList(MainActivity.this, R.layout.list_item, jsonObjectsList);
                    return listAdapter;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(CustomList listAdapter) {
                mListView.setAdapter(listAdapter);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class ViewHolder {
    TextView name;
    TextView endDate;
    TextView state;
    TextView city;
    ImageView icon;

}

class CustomList extends ArrayAdapter<JSONObject> {
    final String NAME = "name", STATE = "state", CITY = "city", ENDDATE = "endDate", VENUE  = "venue", ICON = "icon";
    Context context;
    List<JSONObject> objects;
    ViewHolder viewHolder;

    public CustomList(Context context, int resource, ArrayList<JSONObject> objects) {
        super(context, resource,objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.city = (TextView) convertView.findViewById(R.id.city);
            viewHolder.state = (TextView) convertView.findViewById(R.id.state);
            viewHolder.endDate = (TextView) convertView.findViewById(R.id.end_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        JSONObject jsonObject = objects.get(position);
        try {
            JSONObject venue = jsonObject.getJSONObject(VENUE);
            if(jsonObject.has(ICON)){
                Picasso.with(getContext()).load(jsonObject.getString(ICON)).into(viewHolder.icon);
            }
            if(jsonObject.has(NAME))
                viewHolder.name.setText(jsonObject.getString(NAME));
            if(venue.has(CITY))
                viewHolder.city.setText(venue.getString(CITY));
            if(venue.has(STATE))
                viewHolder.state.setText(venue.getString(STATE));
            if(jsonObject.has(ENDDATE))
                viewHolder.endDate.setText(jsonObject.getString(ENDDATE));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}


