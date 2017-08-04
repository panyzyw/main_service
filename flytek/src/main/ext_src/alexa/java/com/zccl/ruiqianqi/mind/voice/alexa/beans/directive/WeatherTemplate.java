package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

import java.util.List;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class WeatherTemplate extends BaseDirective {
    public Payload payload;

    public WeatherTemplate(){
        header.namespace = "TemplateRuntime";
        header.name = "RenderTemplate";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;
        public String type = "WeatherTemplate";
        /**
         * Body text for the card
         */
        public String textField;
        public BodyTemplate1.Title title;
        public BodyTemplate1.Image skillIcon;

        public String currentWeather;
        public String description;
        public BodyTemplate1.Image currentWeatherIcon;

        public Temperature highTemperature;
        public Temperature lowTemperature;

        public List<Forecast> weatherForecast;
    }

    public static class Temperature{
        public String value;
        public BodyTemplate1.Image arrow;
    }

    public static class Forecast{
        public String day;
        public String date;
        public String highTemperature;
        public String lowTemperature;
        public BodyTemplate1.Image image;
    }

}
