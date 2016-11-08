package app.sunshine.aliniribeiro.com.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        //expandir a view
        View rootView  = inflater.inflate(R.layout.fragment_main, container, false);
        //criar uma lista de array para representar a previsão do tempo
        String []forecastArray = {"Today - Sunny - 88/66", "Today - Sunny - 88/66", "Tomorrow - Foggy - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66",
                "Tomorrow - Foggy - 88/66", "Weds - Cloudy - 72/63",  "Thurs - Rainny - 64/61", "Fri - Foggy - 70/46", "Set - Sunny - 88/66"};
        //crio o adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, forecastArray);
        //crio a listview
        ListView listview  = (ListView) rootView.findViewById(R.id.listview_forecast);
        listview.setAdapter(adapter);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        //Abre a url, faz o request, pega a resposta e desconecta:
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=4fb9361470565d287266db654df62b12");
            // Cria o request para o OpenWeather e abre a conexão;
            urlConnection = (HttpURLConnection) url.openConnection();
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
        } catch (java.io.IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
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
        return rootView;
    }
}
