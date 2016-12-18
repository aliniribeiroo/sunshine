package app.sunshine.aliniribeiro.com.sunshine;

import android.app.Fragment;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ForecastFragment extends Fragment {

    private String forecastJsonStr = null;
    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    //Utilizamos o setHasOptionsMenu para indicar que queremos callbacks dos métodos.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            weatherCast.execute("89035-000");
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
         mForecastAdapter = new ArrayAdapter<String>(
              getActivity(),
                R.layout.list_item_forecast,
                 weekForecast);

        //expandir a view
        View rootView  = inflater.inflate(R.layout.fragment_main, container, false);

        //crio a listview
        ListView listview  = (ListView) rootView.findViewById(R.id.listview_forecast);
        listview.setAdapter(mForecastAdapter);

        return rootView;
    }

    public class FetchWeatherCast extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherCast.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : strings) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }

        /**
         * Método para arredondar as temperaturas
         */
        private String formatHighLows(double high, double low) {
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Método para transformar o String Json da Api em um array com as temperaturas
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            //Usamos calendário Gregoriano para pegar a data
            Calendar dayTime = new GregorianCalendar();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {

                String day;
                String description;
                String highAndLow;

                // Pegamos o Json que representa o dia
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // convertemos valor integer que retornou do Calendar.DAY_OF_WEEK para uma String
                day = dayTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);

                dayTime.add(Calendar.DAY_OF_WEEK, 1);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        public String[] doInBackground(String... params) {
            
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            //Abre a url, faz o request, pega a resposta e desconecta:
            try {

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays)).appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


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

          try {
              return getWeatherDataFromJson(forecastJsonStr, numDays);
          }catch (JSONException e){
              Log.e(LOG_TAG,e.getMessage(),e);
              e.printStackTrace();
          }
          // Apenas retornamos null caso ocorra algum erro.
          return null;
        }
    }
}
