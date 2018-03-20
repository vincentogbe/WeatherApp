package com.example.vincentogbe.zone;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by vincent ogbe on 07/11/2017.
 */

public class fragment_zone extends Fragment{
    private String tempLight = "LOADING LIGHT...";
    public boolean beacon = true;
    public Double curLight = 0.0;
    public Double curTemp = 0.0;
    public JSONObject curWth;

    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;
    public fragment_zone(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_zone, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new city_zone(getActivity()).getCity());
    }
    private void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = weather_api.getJSON(getActivity(), city);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            curWth = json;
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }
    @SuppressLint("SetTextI18n")
    public void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            if (beacon){
                tempLight = lightLevel(curLight);

                detailsField.setText(details.getString("description").toUpperCase(Locale.US)+ "\n"
                        + "Humidity: " + main.getString("humidity") + "%" + "\n"
                        + "Pressure: " + main.getString("pressure") + " hPa" + "\n"
                        + "Light: " + tempLight);

                currentTemperatureField.setText(curTemp + " ℃");
            }
            else {
                detailsField.setText(details.getString("description").toUpperCase(Locale.US) + "\n"
                        + "Humidity: " + main.getString("humidity") + "%" + "\n"
                        + "Pressure: " + main.getString("pressure") + " hPa");

                currentTemperatureField.setText(main.getDouble("temp") + " ℃");
            }
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }

    public String lightLevel(Double light)
    {

        if(light < 2)
        {
            return "Very Dark";
        }// end if
        else if(light >= 2 && light < 50)
        {
            return "Dark";
        }// end else if
        else if(light >= 50 && light < 100)
        {
            return "Low Light";
        }// end else if
        else if(light >= 100 && light < 750)
        {
            return "Overcast and Dark";
        }// end elseif
        else if(light >= 750 && light < 2500)
        {
            return "Overcast";
        }// end else if
        else if(light >= 2500 && light < 5000)
        {
            return "Average";
        }// end else if
        else if(light >= 5000 && light < 10000)
        {
            return "Nice out";
        }// end else if
        else if(light >= 10000 && light < 29000)
        {
            return "Sunny";
        }// end else if
        else if(light >= 29000)
        {
            return "Very Bright";
        }// end else if

        return "ERROR";
    }
}


