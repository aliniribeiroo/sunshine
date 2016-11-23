package app.sunshine.aliniribeiro.com.sunshine;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */

public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    //Utilizamos o setHasOptionsMenu para indicar que queremos callbacks dos métodos.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    //Chamamos para inflar o menu que criamos:forecastfragment.xml
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //Se o item do menu é selecionado, instanciamos a nossa classe FetchWheatherCast e a executamos.
        if (id == R.id.action_refresh) {
            FetchWeatherCast weatherCast = new FetchWeatherCast();
            weatherCast.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        //criar uma lista de array para representar a previsão do tempo
        String []forecastArray = {"Today - Sunny - 88/66", "Today - Sunny - 88/66", "Tomorrow - Foggy - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66"};

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        //crio o adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter <String>(
              getActivity(),
                R.layout.list_item_forecast,
                forecastArray);

        //expandir a viewx
        View rootView  = inflater.inflate(R.layout.fragment_main, container, false);

        //crio a listview
        ListView listview  = (ListView) rootView.findViewById(R.id.listview_forecast);
        listview.setAdapter(adapter);



        return rootView;
    }

    public class FetchWeatherCast extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherCast.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            //Abre a url, faz o request, pega a resposta e desconecta:
            try {
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=4fb9361470565d287266db654df62b12";
                URL url = new URL(baseUrl.concat(apiKey));


                // Cria o request para o OpenWeather e abre a conexão;
                urlConnection = (HttpURLConnection)  url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Lê o stream e salva como uma string;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: "+ forecastJsonStr);

            } catch (java.io.IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader!= null){
                    try {
                        reader.close();
                    }catch (final IOException e){
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
