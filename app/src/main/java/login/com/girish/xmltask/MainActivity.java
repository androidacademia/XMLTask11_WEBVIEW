package login.com.girish.xmltask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
    }

    public void download(View view) {
        if (ConnectivityStatus.isConnected(this)){
            XMLTask xmlTask = new XMLTask();
            xmlTask.execute("https://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=e242949c082b0e914a2f46b05f41eacf&mode=xml");
        }
    }

    private class XMLTask extends AsyncTask<String,Void,Weather>{

        @Override
        protected Weather doInBackground(String... strings) {
            String str = strings[0];
            Weather weather = null;
            try {
                URL url = new URL(str);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                ////////////////////////////////////////////////////////////
                //// Do the conversion//////////////////////////////////////
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(inputStream,null);//null is the utf
                //Now parse it.........
                int eventType = xmlPullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            weather = new Weather();
                            break;
                        case XmlPullParser.START_TAG:
                            if (xmlPullParser.getName().equals("city")){
                                //dosomething
                                String city = xmlPullParser.getAttributeValue(1);
                                int count = xmlPullParser.getAttributeCount();
                                /*
                                for (int i = 0; i<count;i++){
                                    xmlPullParser.getAttributeValue(i);
                                }*/
                                weather.setCity(city);
                            }else if (xmlPullParser.getName().equals("country")){
                                String country = xmlPullParser.nextText();
                                weather.setCountry(country);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType = xmlPullParser.next();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            if (weather!=null){
                String result = "Name :"+weather.getCity()+"\n" +
                        "Country :"+weather.getCountry();
                textView.setText(result);
            }


        }
    }

}














