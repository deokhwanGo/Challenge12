package com.example.gdh.afterandroid;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class sampleW extends AppCompatActivity {
    public static final int THREAD_HANDLER_SUCCESS_INFO = 1;
    TextView tv_WeatherInfo;

    ForeCastManager mForeCast;

    String lon = "128.3910799"; // 좌표 설정
    String lat = "36.1444292";  // 좌표 설정
    MainActivity mThis;
    ArrayList<ContentValues> mWeatherData;
    ArrayList<WeatherInfo> mWeatherInfomation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_w);
        Initialize();
    }

    public void Initialize() {
        tv_WeatherInfo = (TextView) findViewById(R.id.tv_WeatherInfo);
        mWeatherInfomation = new ArrayList<>();
//        mThis = this;
        mForeCast = new ForeCastManager(lon, lat, mThis);
        mForeCast.run();
    }

    public String PrintValue() {
        String mData = "";
        for (int i = 0; i < mWeatherInfomation.size(); i++) {
            mData = mData + mWeatherInfomation.get(i).getWeather_Day() + "\r\n"
                    + mWeatherInfomation.get(i).getWeather_Name() + "\r\n"
                    + mWeatherInfomation.get(i).getClouds_Sort()
                    + " /Cloud amount: " + mWeatherInfomation.get(i).getClouds_Value()
                    + mWeatherInfomation.get(i).getClouds_Per() + "\r\n"
                    + mWeatherInfomation.get(i).getWind_Name()
                    + " /WindSpeed: " + mWeatherInfomation.get(i).getWind_Speed() + " mps" + "\r\n"
                    + "Max: " + mWeatherInfomation.get(i).getTemp_Max() + "℃"
                    + " /Min: " + mWeatherInfomation.get(i).getTemp_Min() + "℃" + "\r\n"
                    + "Humidity: " + mWeatherInfomation.get(i).getHumidity() + "%";

            mData = mData + "\r\n" + "----------------------------------------------" + "\r\n";
        }
        return mData;
    }

    public void DataChangedToHangeul() {
        for (int i = 0; i < mWeatherInfomation.size(); i++) {
            WeatherToHangeul mHangeul = new WeatherToHangeul(mWeatherInfomation.get(i));
            mWeatherInfomation.set(i, mHangeul.getHangeulWeather());
        }
    }


    public void DataToInformation() {
        for (int i = 0; i < mWeatherData.size(); i++) {
            mWeatherInfomation.add(new WeatherInfo(
                    String.valueOf(mWeatherData.get(i).get("weather_Name")), String.valueOf(mWeatherData.get(i).get("weather_Number")), String.valueOf(mWeatherData.get(i).get("weather_Much")),
                    String.valueOf(mWeatherData.get(i).get("weather_Type")), String.valueOf(mWeatherData.get(i).get("wind_Direction")), String.valueOf(mWeatherData.get(i).get("wind_SortNumber")),
                    String.valueOf(mWeatherData.get(i).get("wind_SortCode")), String.valueOf(mWeatherData.get(i).get("wind_Speed")), String.valueOf(mWeatherData.get(i).get("wind_Name")),
                    String.valueOf(mWeatherData.get(i).get("temp_Min")), String.valueOf(mWeatherData.get(i).get("temp_Max")), String.valueOf(mWeatherData.get(i).get("humidity")),
                    String.valueOf(mWeatherData.get(i).get("Clouds_Value")), String.valueOf(mWeatherData.get(i).get("Clouds_Sort")), String.valueOf(mWeatherData.get(i).get("Clouds_Per")), String.valueOf(mWeatherData.get(i).get("day"))
            ));

        }

    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case THREAD_HANDLER_SUCCESS_INFO:
                    mForeCast.getmWeather();
                    mWeatherData = mForeCast.getmWeather();
                    if (mWeatherData.size() == 0)
                        tv_WeatherInfo.setText("데이터가 없습니다");

                    DataToInformation(); // 자료 클래스로 저장,


                    String data = "";
                    data = PrintValue();
                    DataChangedToHangeul();
                    data = data + PrintValue();

                    tv_WeatherInfo.setText(data);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Created by Warguss on 2016-01-18.
     */

    public class ForeCastManager extends Thread {

        String lon, lat;

        ArrayList<ContentValues> mWeatehr;
        MainActivity mContext;

        public ArrayList<ContentValues> getmWeather() {
            return mWeatehr;
        }

        public ForeCastManager(String lon, String lat, MainActivity mContext) {
            this.lon = lon;
            this.lat = lat;
            this.mContext = mContext;
        }


        public ArrayList<ContentValues> GetOpenWeather(String lon, String lat) {

            ArrayList<ContentValues> mTotalValue = new ArrayList<ContentValues>();
            String key = "e580c61383a1cce1091eb3456c5ca256";
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?" +
                        "&APPID=" + key +
                        "&lat=" + lat +
                        "&lon=" + lon +
                        "&mode=xml" +
                        "&units=metric" +
                        "&cnt=" + 15);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // 위에서 생성된 URL을 통하여 서버에 요청하면 결과가 XML Resource로 전달됨
                XmlPullParser parser = factory.newPullParser();
                // XML Resource를 파싱할 parser를 factory로 생성
                parser.setInput(url.openStream(), null);
                // 파서를 통하여 각 요소들의 이벤트성 처리를 반복수행
                int parserEvent = parser.getEventType();
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    if (parserEvent == XmlPullParser.START_TAG && parser.getName().equals("time")) {
                        //시작태그의 이름을 알아냄
                        int checkStartTag = parserEvent;
                        ContentValues mContent = new ContentValues();

                        for (; ; ) {
                            if (checkStartTag == XmlPullParser.START_TAG && parser.getName().equals("time")) {
                                mContent.put("day", parser.getAttributeValue(null, "day"));
                            } else if (checkStartTag == XmlPullParser.START_TAG && parser.getName().equals("symbol")) {
                                mContent.put("weather_Name", parser.getAttributeValue(null, "name"));
                                mContent.put("weather_Number", parser.getAttributeValue(null, "number"));
                            } else if (checkStartTag == XmlPullParser.START_TAG &&
                                    parser.getName().equals("precipitation")) {
                                mContent.put("weather_Much", parser.getAttributeValue(null, "value"));
                                mContent.put("weather_Type", parser.getAttributeValue(null, "type"));
                            } else if (checkStartTag == XmlPullParser.START_TAG &&
                                    parser.getName().equals("windDirection")) {
                                mContent.put("wind_Direction", parser.getAttributeValue(null, "name"));
                                mContent.put("wind_SortNumber", parser.getAttributeValue(null, "deg"));
                                mContent.put("wind_SortCode", parser.getAttributeValue(null, "code"));
                            } else if (checkStartTag == XmlPullParser.START_TAG && parser.getName().equals("windSpeed")) {
                                mContent.put("wind_Speed", parser.getAttributeValue(null, "mps"));
                                mContent.put("wind_Name", parser.getAttributeValue(null, "name"));
                            } else if (checkStartTag == XmlPullParser.START_TAG &&
                                    parser.getName().equals("temperature")) {
                                mContent.put("temp_Min", parser.getAttributeValue(null, "min"));
                                mContent.put("temp_Max", parser.getAttributeValue(null, "max"));
                            } else if (checkStartTag == XmlPullParser.START_TAG && parser.getName().equals("humidity")) {
                                mContent.put("humidity", parser.getAttributeValue(null, "value"));
                                mContent.put("humidity_unit", parser.getAttributeValue(null, "unit"));
                            } else if (checkStartTag == XmlPullParser.START_TAG && parser.getName().equals("clouds")) {
                                mContent.put("Clouds_Sort", parser.getAttributeValue(null, "value"));
                                mContent.put("Clouds_Value", parser.getAttributeValue(null, "all"));
                                mContent.put("Clouds_Per", parser.getAttributeValue(null, "unit"));
                                mTotalValue.add(mContent);
                                break;
                            }
                            checkStartTag = parser.next();
                        }

                    }
                    parserEvent = parser.next();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mTotalValue;
        }


        @Override
        public void run() {
            super.run();
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            mWeatehr = GetOpenWeather(lon, lat);
            //mContext.handler.sendEmptyMessage(mContext.THREAD_HANDLER_SUCCESS_INFO);
            //Thread 작업 종료, UI 작업을 위해 MainHandler에 Message보냄    }
        }


    }
    /** * Created by Warguss on 2016-01-18. */public class WeatherInfo {
        String weather_Day;
        String weather_Name;
        String weather_Number;
        String weather_Much;
        String weather_Type;
        String wind_Direction;
        String wind_SortNumber;
        String wind_SortCode;
        String wind_Speed;
        String wind_Name;
        String temp_Min;
        String temp_Max;
        String humidity;
        String clouds_Value;
        String clouds_Sort;
        String clouds_Per;

        public WeatherInfo(String weather_Name, String weather_Number, String weather_Much,
                           String weather_Type, String wind_Direction, String wind_SortNumber,
                           String wind_SortCode, String wind_Speed, String wind_Name,
                           String temp_Min, String temp_Max, String humidity,
                           String clouds_Value, String clouds_Sort, String clouds_Per,String weather_Day)
        {
            this.weather_Name = weather_Name;
            this.weather_Number = weather_Number;
            this.weather_Much = weather_Much;
            this.weather_Type = weather_Type;
            this.wind_Direction = wind_Direction;
            this.wind_SortNumber = wind_SortNumber;
            this.wind_SortCode = wind_SortCode;
            this.wind_Speed = wind_Speed;

            if(wind_Name.equals("")) this.wind_Name = "No Info";
            else this.wind_Name = wind_Name;

            this.temp_Min = temp_Min;
            this.temp_Max = temp_Max;
            this.humidity = humidity;
            this.clouds_Value = clouds_Value;
            this.clouds_Sort = clouds_Sort;
            this.clouds_Per = clouds_Per;
            this.weather_Day = weather_Day;


        }

        public String getWeather_Name() {
            return weather_Name;
        }

        public String getWind_Speed() {
            return wind_Speed;
        }

        public String getWind_Name() {
            return wind_Name;
        }

        public String getTemp_Min() {
            return temp_Min;
        }

        public String getTemp_Max() {
            return temp_Max;
        }

        public String getHumidity() {
            return humidity;
        }

        public String getClouds_Value() {
            return clouds_Value;
        }

        public String getClouds_Sort() {
            return clouds_Sort;
        }

        public String getClouds_Per() {
            return clouds_Per;
        }

        public String getWeather_Day() { return weather_Day; }

        public void setWeather_Name(String weather_Name) { this.weather_Name = weather_Name; }

        public void setWind_Name(String wind_Name) { this.wind_Name = wind_Name; }

        public void setClouds_Sort(String clouds_Sort) {this.clouds_Sort = clouds_Sort;}
    }

    /** * Created by Warguss on 2016-01-18. */
    public class WeatherConditionList {
        public ArrayList<WeatherCondition> mListSnow;
        public ArrayList<WeatherCondition> mListClearSky;
        public ArrayList<WeatherCondition> mListFew_Clouds;
        public ArrayList<WeatherCondition> mListScattered_Clouds;
        public ArrayList<WeatherCondition> mListBroken_Clouds;
        public ArrayList<WeatherCondition> mListShower_Rain;
        public ArrayList<WeatherCondition> mListRain;
        public ArrayList<WeatherCondition> mListThunderStorm;
        public ArrayList<WeatherCondition> mListMist;
        public ArrayList<WeatherCondition> mListWind;
        public ArrayList<WeatherCondition> mListWindDirection;

        public ArrayList<WeatherCondition> mListSnowToHangeul;
        public ArrayList<WeatherCondition> mListClearSkyToHangeul;
        public ArrayList<WeatherCondition> mListFew_CloudsToHangeul;
        public ArrayList<WeatherCondition> mListScattered_CloudsToHangeul;
        public ArrayList<WeatherCondition> mListBroken_CloudsToHangeul;
        public ArrayList<WeatherCondition> mListShower_RainToHangeul;
        public ArrayList<WeatherCondition> mListRainToHangeul;
        public ArrayList<WeatherCondition> mListThunderStormToHangeul;
        public ArrayList<WeatherCondition> mListMistToHangeul;
        public ArrayList<WeatherCondition> mListWindToHangeul;
        public ArrayList<WeatherCondition> mListWindDirectionToHangeul;

        public WeatherConditionList() {
            //http://openweathermap.org/weather-conditions
            mListThunderStorm = new ArrayList<WeatherCondition>(); // 11
            mListMist = new ArrayList<WeatherCondition>();        // 50
            mListRain = new ArrayList<WeatherCondition>();       // 10
            mListShower_Rain = new ArrayList<WeatherCondition>(); // 09
            mListBroken_Clouds = new ArrayList<WeatherCondition>(); // 04
            mListScattered_Clouds = new ArrayList<WeatherCondition>(); // 03
            mListFew_Clouds = new ArrayList<WeatherCondition>(); // 02
            mListClearSky = new ArrayList<WeatherCondition>(); // 01
            mListSnow = new ArrayList<WeatherCondition>(); // 13
            mListWind = new ArrayList<WeatherCondition>();
            mListWindDirection = new ArrayList<WeatherCondition>();
            mListThunderStormToHangeul = new ArrayList<WeatherCondition>(); // 11
            mListMistToHangeul = new ArrayList<WeatherCondition>();        // 50
            mListRainToHangeul = new ArrayList<WeatherCondition>();       // 10
            mListShower_RainToHangeul = new ArrayList<WeatherCondition>(); // 09
            mListBroken_CloudsToHangeul = new ArrayList<WeatherCondition>(); // 04
            mListScattered_CloudsToHangeul = new ArrayList<WeatherCondition>(); // 03
            mListFew_CloudsToHangeul = new ArrayList<WeatherCondition>(); // 02
            mListClearSkyToHangeul = new ArrayList<WeatherCondition>(); // 01
            mListSnowToHangeul = new ArrayList<WeatherCondition>(); // 13
            mListWindToHangeul = new ArrayList<WeatherCondition>();
            mListWindDirectionToHangeul = new ArrayList<WeatherCondition>();
            //-------------ThunderStrom------------------//
            mListThunderStorm.add(new WeatherCondition("200","thunderstorm with light rain"));
            mListThunderStorm.add(new WeatherCondition("201","thunderstorm with rain"));
            mListThunderStorm.add(new WeatherCondition("202","thunderstorm with heavy rain"));
            mListThunderStorm.add(new WeatherCondition("210","light thunderstorm"));
            mListThunderStorm.add(new WeatherCondition("211","thunderstorm"));
            mListThunderStorm.add(new WeatherCondition("212","heavy thunderstorm"));
            mListThunderStorm.add(new WeatherCondition("221","ragged thunderstorm"));
            mListThunderStorm.add(new WeatherCondition("230","thunderstorm with light drizzle"));
            mListThunderStorm.add(new WeatherCondition("231","thunderstorm with drizzle"));
            mListThunderStorm.add(new WeatherCondition("232","thunderstorm with heavy drizzle"));

            mListThunderStormToHangeul.add(new WeatherCondition("200","번개와 보슬비"));
            mListThunderStormToHangeul.add(new WeatherCondition("201","번개와 비"));
            mListThunderStormToHangeul.add(new WeatherCondition("202","번개와 집중 호우"));
            mListThunderStormToHangeul.add(new WeatherCondition("210","천둥"));
            mListThunderStormToHangeul.add(new WeatherCondition("211","천둥 번개"));
            mListThunderStormToHangeul.add(new WeatherCondition("212","강한 천둥 번개"));
            mListThunderStormToHangeul.add(new WeatherCondition("221","매우 강한 천둥 번개"));
            mListThunderStormToHangeul.add(new WeatherCondition("230","번개와 가벼운 이슬비"));
            mListThunderStormToHangeul.add(new WeatherCondition("231","번개와 이슬비"));
            mListThunderStormToHangeul.add(new WeatherCondition("232","번개와 집중 호우"));

            //------------Drizzle-------------------//
            mListShower_Rain.add(new WeatherCondition("300","light intensity drizzle"));
            mListShower_Rain.add(new WeatherCondition("301","drizzle"));
            mListShower_Rain.add(new WeatherCondition("302","heavy intensity drizzle"));
            mListShower_Rain.add(new WeatherCondition("310","light intensity drizzle rain"));
            mListShower_Rain.add(new WeatherCondition("311","drizzle rain"));
            mListShower_Rain.add(new WeatherCondition("312","heavy intensity drizzle rain"));
            mListShower_Rain.add(new WeatherCondition("313","shower rain and drizzle"));
            mListShower_Rain.add(new WeatherCondition("314","heavy shower rain and drizzle"));
            mListShower_Rain.add(new WeatherCondition("321","shower drizzle"));

            mListShower_RainToHangeul.add(new WeatherCondition("300","약한 이슬비"));
            mListShower_RainToHangeul.add(new WeatherCondition("301","이슬비"));
            mListShower_RainToHangeul.add(new WeatherCondition("302","강한 이슬비"));
            mListShower_RainToHangeul.add(new WeatherCondition("310","약한 이슬비"));
            mListShower_RainToHangeul.add(new WeatherCondition("311","이슬비"));
            mListShower_RainToHangeul.add(new WeatherCondition("312","강한 이슬비"));
            mListShower_RainToHangeul.add(new WeatherCondition("313","소나기"));
            mListShower_RainToHangeul.add(new WeatherCondition("314","강한 소나기"));
            mListShower_RainToHangeul.add(new WeatherCondition("321","소나기"));
            //------------Rain----------------------//
            mListRain.add(new WeatherCondition("500","light rain"));
            mListRain.add(new WeatherCondition("501","moderate rain"));
            mListRain.add(new WeatherCondition("502","heavy intensity rain"));
            mListRain.add(new WeatherCondition("503","very heavy rain"));
            mListRain.add(new WeatherCondition("504","extreme rain"));
            mListSnow.add(new WeatherCondition("511","freezing rain"));
            mListShower_Rain.add(new WeatherCondition("520","light intensity shower rain"));
            mListShower_Rain.add(new WeatherCondition("521","shower rain"));
            mListShower_Rain.add(new WeatherCondition("522","heavy intensity shower rain"));
            mListShower_Rain.add(new WeatherCondition("531","ragged shower rain"));

            mListRainToHangeul.add(new WeatherCondition("500","가벼운 비"));
            mListRainToHangeul.add(new WeatherCondition("501","비"));
            mListRainToHangeul.add(new WeatherCondition("502","강한 비"));
            mListRainToHangeul.add(new WeatherCondition("503","집중 호우"));
            mListRainToHangeul.add(new WeatherCondition("504","집중 호우"));
            mListSnowToHangeul.add(new WeatherCondition("511","어는 비"));
            mListShower_RainToHangeul.add(new WeatherCondition("520","가벼운 소나기"));
            mListShower_RainToHangeul.add(new WeatherCondition("521","소나기"));
            mListShower_RainToHangeul.add(new WeatherCondition("522","강한 소나기"));
            mListShower_RainToHangeul.add(new WeatherCondition("531","매우 강한 소나기"));

            //------------Snow----------------------//
            mListSnow.add(new WeatherCondition("600","light snow"));
            mListSnow.add(new WeatherCondition("601","snow"));
            mListSnow.add(new WeatherCondition("602","heavy snow"));
            mListSnow.add(new WeatherCondition("611","sleet"));
            mListSnow.add(new WeatherCondition("612","shower sleet"));
            mListSnow.add(new WeatherCondition("615","light rain and snow"));
            mListSnow.add(new WeatherCondition("616","rain and snow"));
            mListSnow.add(new WeatherCondition("620","light shower snow"));
            mListSnow.add(new WeatherCondition("621","shower snow"));
            mListSnow.add(new WeatherCondition("622","heavy shower snow"));

            mListSnowToHangeul.add(new WeatherCondition("600","약한 눈"));
            mListSnowToHangeul.add(new WeatherCondition("601","눈"));
            mListSnowToHangeul.add(new WeatherCondition("602","거센 눈"));
            mListSnowToHangeul.add(new WeatherCondition("611","진눈 깨비"));
            mListSnowToHangeul.add(new WeatherCondition("612","급 진눈 깨비"));
            mListSnowToHangeul.add(new WeatherCondition("615","약한 눈과 비"));
            mListSnowToHangeul.add(new WeatherCondition("616","눈과 비"));
            mListSnowToHangeul.add(new WeatherCondition("620","눈"));
            mListSnowToHangeul.add(new WeatherCondition("621","소낙눈"));
            mListSnowToHangeul.add(new WeatherCondition("622","강한 소낙눈"));
            //------------Atmosphere----------------------//
            mListMist.add(new WeatherCondition("701","mist"));
            mListMist.add(new WeatherCondition("711","smoke"));
            mListMist.add(new WeatherCondition("721","haze"));
            mListMist.add(new WeatherCondition("731","sand, dust whirls"));
            mListMist.add(new WeatherCondition("741","fog"));
            mListMist.add(new WeatherCondition("751","sand"));
            mListMist.add(new WeatherCondition("761","dust"));
            mListMist.add(new WeatherCondition("762","volcanic ash"));
            mListMist.add(new WeatherCondition("771","squalls"));
            mListMist.add(new WeatherCondition("781","tornado"));

            mListMistToHangeul.add(new WeatherCondition("701","안개"));
            mListMistToHangeul.add(new WeatherCondition("711","연기"));
            mListMistToHangeul.add(new WeatherCondition("721","실안개"));
            mListMistToHangeul.add(new WeatherCondition("731","황사 바람"));
            mListMistToHangeul.add(new WeatherCondition("741","안개"));
            mListMistToHangeul.add(new WeatherCondition("751","황사"));
            mListMistToHangeul.add(new WeatherCondition("761","황사"));
            mListMistToHangeul.add(new WeatherCondition("762","화산재"));
            mListMistToHangeul.add(new WeatherCondition("771","돌풍"));
            mListMistToHangeul.add(new WeatherCondition("781","태풍"));

            //------------clouds----------------------//
            mListClearSky.add(new WeatherCondition("800","clear sky"));
            mListFew_Clouds.add(new WeatherCondition("801","few clouds"));
            mListScattered_Clouds.add(new WeatherCondition("802","scattered clouds"));
            mListBroken_Clouds.add(new WeatherCondition("803","broken clouds"));
            mListBroken_Clouds.add(new WeatherCondition("804","overcast clouds"));

            mListClearSkyToHangeul.add(new WeatherCondition("800","맑은 하늘"));
            mListFew_CloudsToHangeul.add(new WeatherCondition("801","구름 조금"));
            mListScattered_CloudsToHangeul.add(new WeatherCondition("802","조각 구름"));
            mListBroken_CloudsToHangeul.add(new WeatherCondition("803","조각 구름"));
            mListBroken_CloudsToHangeul.add(new WeatherCondition("804","흐림"));

            //-----------------Additional----------//
            mListWind.add(new WeatherCondition("951","calm"));
            mListWind.add(new WeatherCondition("952","light breeze"));
            mListWind.add(new WeatherCondition("953","gentle breeze"));
            mListWind.add(new WeatherCondition("954","moderate breeze"));
            mListWind.add(new WeatherCondition("955","fresh breeze"));
            mListWind.add(new WeatherCondition("956","strong breeze"));
            mListWind.add(new WeatherCondition("957","high wind, near gale"));
            mListWind.add(new WeatherCondition("958","gale"));
            mListWind.add(new WeatherCondition("959","severe gale"));
            mListWind.add(new WeatherCondition("960","storm"));
            mListWind.add(new WeatherCondition("961","violent storm"));
            mListWind.add(new WeatherCondition("962","hurricane"));

            mListWindToHangeul.add(new WeatherCondition("951","바람 없음"));
            mListWindToHangeul.add(new WeatherCondition("952","남실 바람"));
            mListWindToHangeul.add(new WeatherCondition("953","산들 바람"));
            mListWindToHangeul.add(new WeatherCondition("954","건들 바람"));
            mListWindToHangeul.add(new WeatherCondition("955","흔들 바람"));
            mListWindToHangeul.add(new WeatherCondition("956","된바람"));
            mListWindToHangeul.add(new WeatherCondition("957","센바람"));
            mListWindToHangeul.add(new WeatherCondition("958","강풍"));
            mListWindToHangeul.add(new WeatherCondition("959","극심한 강풍"));
            mListWindToHangeul.add(new WeatherCondition("960","폭풍우"));
            mListWindToHangeul.add(new WeatherCondition("961","폭풍"));
            mListWindToHangeul.add(new WeatherCondition("962","허리케인"));
        }



        public class WeatherCondition {
            String id;
            String meaning;

            public WeatherCondition(String id, String meaning) {
                this.id = id;
                this.meaning = meaning;
            }
            public String getId() {
                return id;
            }
            public String getMeaning() {
                return meaning;
            }
        }
    }
    /** * Created by Warguss on 2016-01-18. */
    public class WeatherToHangeul {
        WeatherConditionList mCondition;
        ContentValues mData;
        WeatherInfo mWeatherInfo;

        public WeatherToHangeul(WeatherInfo tData)
        {
            mCondition = new WeatherConditionList();
            mWeatherInfo = tData;


            mWeatherInfo.setClouds_Sort(Hangeul_Weather(mWeatherInfo.clouds_Sort));
            mWeatherInfo.setWeather_Name(Hangeul_Weather(mWeatherInfo.weather_Number));
            mWeatherInfo.setWind_Name(Hangeul_Weather(mWeatherInfo.wind_Name));
        }

        public WeatherInfo getHangeulWeather()
        {
            return mWeatherInfo;
        }

        public String SnowToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListSnow.size() ; i++)
            {
                if(mCondition.mListSnow.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListSnow.get(i).getMeaning().equals(weatherNumber))
                    return mCondition.mListSnowToHangeul.get(i).getMeaning();
            }

            return "";
        }
        public String ClearToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListClearSky.size() ; i++)
            {
                if(mCondition.mListClearSky.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListClearSky.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListClearSkyToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String BrokenCloudsToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListBroken_Clouds.size() ; i++)
            {
                if(mCondition.mListBroken_Clouds.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListBroken_Clouds.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListBroken_CloudsToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String FewCloudsToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListFew_Clouds.size() ; i++)
            {
                if(mCondition.mListFew_Clouds.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListFew_Clouds.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListFew_CloudsToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String ScatteredCloudsToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListScattered_Clouds.size() ; i++)
            {
                if(mCondition.mListScattered_Clouds.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListScattered_Clouds.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListScattered_CloudsToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String RainToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListRain.size() ; i++)
            {
                if(mCondition.mListRain.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListRain.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListRainToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String ShowerRainToHanGeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListShower_Rain.size() ; i++)
            {
                if(mCondition.mListShower_Rain.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListShower_Rain.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListShower_RainToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String ThunderStromToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListThunderStorm.size() ; i++)
            {
                if(mCondition.mListThunderStorm.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListThunderStorm.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListThunderStormToHangeul.get(i).getMeaning();
            }
            return "";
        }
        public String MistToHangeul(String weatherNumber)
        {
            for(int i = 0; i < mCondition.mListMist.size() ; i++)
            {
                if(mCondition.mListMist.get(i).getId().equals(weatherNumber) ||
                        mCondition.mListMist.get(i).getMeaning().equals(weatherNumber.toLowerCase()))
                    return mCondition.mListMistToHangeul.get(i).getMeaning();
            }
            return "";
        }

        public String WindToHangeul(String windName)
        {
            for(int i = 0; i < mCondition.mListWind.size() ; i++) {
                if(mCondition.mListWind.get(i).getId().equals(windName)
                        || mCondition.mListWind.get(i).getMeaning().equals(windName.toLowerCase()))
                    return mCondition.mListWindToHangeul.get(i).getMeaning();
            }
            return "";
        }

        public String Hangeul_Weather(String mWeatherNumber)
        {
            String snow = SnowToHangeul(mWeatherNumber);
            String clear = ClearToHangeul(mWeatherNumber);
            String broken_Cloud = BrokenCloudsToHangeul(mWeatherNumber);
            String few_Cloud = FewCloudsToHangeul(mWeatherNumber);
            String scatter = ScatteredCloudsToHangeul(mWeatherNumber);
            String Rain = RainToHangeul(mWeatherNumber);
            String shower = ShowerRainToHanGeul(mWeatherNumber);
            String thunder = ThunderStromToHangeul(mWeatherNumber);
            String mist = MistToHangeul(mWeatherNumber);
            String wind = WindToHangeul(mWeatherNumber);


            if(!snow.equals("")) return snow;
            else if(!clear.equals("")) return clear;
            else if(!broken_Cloud.equals("")) return broken_Cloud;
            else if(!few_Cloud.equals("")) return few_Cloud;
            else if(!scatter.equals("")) return scatter;
            else if(!Rain.equals("")) return Rain;
            else if(!shower.equals("")) return shower;
            else if(!thunder.equals("")) return thunder;
            else if(!mist.equals("")) return mist;
            else if(!wind.equals("")) return wind;

            return "정보없음";
        }
    }

}
