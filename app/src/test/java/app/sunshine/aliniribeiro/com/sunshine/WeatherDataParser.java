package app.sunshine.aliniribeiro.com.sunshine;

/**
 * Created by Alini on 24/11/2016.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {

    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException {
        //Transforma a string em o JObject
        JSONObject city = new JSONObject(weatherJsonStr);
        //Cria um Array com as informações que estão na tag List
        JSONArray listOfDays = city.getJSONArray("list");
        JSONObject dayInfo = listOfDays.getJSONObject(dayIndex);// busca as informações do index do array de acordo com a variável dayIndex
        JSONObject temperatureinfo = dayInfo.getJSONObject("temp");
        return temperatureinfo.getDouble("max");
    }
}
