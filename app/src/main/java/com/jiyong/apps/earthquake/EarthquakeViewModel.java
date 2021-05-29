package com.jiyong.apps.earthquake;

import android.app.Application;
import android.location.Location;
import android.util.Log;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EarthquakeViewModel extends AndroidViewModel {
    private static final String TAG="EarthquakeUpdate";
    private LiveData<List<Earthquake>> earthquakes;
    public EarthquakeViewModel(Application application) {
        super(application);
    }
    public LiveData<List<Earthquake>> getEarthquakes(){
        if(earthquakes==null){
            earthquakes=EarthquakeDatabaseAccessor.getInstance(getApplication()).earthquakeDAO().loadAllEarthquakes();
            loadEarthquakes();
        }
        return earthquakes;
    }
    //인터넷 연결 후 xml데이터 파싱
    public void loadEarthquakes(){
        Single.fromCallable(new Callable<List<Earthquake>>(){
            @Override
            public List<Earthquake> call() throws Exception {
                ArrayList<Earthquake> earthquakes=new ArrayList<>(0);
                URL url;
                try{
                    String quakeFeed=getApplication().getString(R.string.earthquake_feed);
                    url=new URL(quakeFeed);
                    URLConnection connection;
                    connection=url.openConnection();
                    HttpsURLConnection httpsConnection=(HttpsURLConnection)connection;
                    int responseCode=httpsConnection.getResponseCode();
                    if(responseCode== HttpURLConnection.HTTP_OK){
                        InputStream in=httpsConnection.getInputStream();
                        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                        DocumentBuilder db=dbf.newDocumentBuilder();
                        Document dom=db.parse(in);
                        Element docEle=dom.getDocumentElement();
                        NodeList nl=docEle.getElementsByTagName("entry");
                        if(nl!=null && nl.getLength()>0){
                            for (int i = 0; i < nl.getLength(); i++) {
                                Element entry=(Element)nl.item(i);
                                Element id=(Element)entry.getElementsByTagName("id").item(0);
                                Element title=(Element)entry.getElementsByTagName("title").item(0);
                                Element g=(Element)entry.getElementsByTagName("georss:point").item(0);
                                Element summary=(Element)entry.getElementsByTagName("summary").item(0);
                                String[] summarySplit=summary.getFirstChild().getNodeValue().split("</p>");
                                StringReader sr=new StringReader(summarySplit[summarySplit.length-1]);
                                InputSource is=new InputSource(sr);
                                Document doc=db.parse(is);
                                Element docElement=doc.getDocumentElement();
                                Element when=(Element)docElement.getElementsByTagName("dd").item(0);
                                Element link=(Element)entry.getElementsByTagName("link").item(0);
                                String idString=id.getFirstChild().getNodeValue();
                                String details=title.getFirstChild().getNodeValue();
                                String hostname="http://earthquake.usgs.gov";
                                String linkString=hostname+link.getAttribute("href");
                                String point=g.getFirstChild().getNodeValue();
                                String dt=when.getFirstChild().getNodeValue().substring(0, 19);
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
                                Date qdate=new GregorianCalendar(0,0,0).getTime();
                                try{
                                    qdate=sdf.parse(dt);
                                }
                                catch (ParseException e){
                                    Log.e(TAG, "Date parsing exception", e);
                                }
                                String[] location=point.split(" ");
                                Location l=new Location("dummyGPS");
                                l.setLatitude(Double.parseDouble(location[0]));
                                l.setLongitude(Double.parseDouble(location[1]));
                                String magnitudeString=details.split(" ")[1];
                                int end=magnitudeString.length()-1;
                                double magnitude=Double.parseDouble(magnitudeString);
                                if(details.contains("-")){
                                    details=details.split("-")[1].trim();
                                }
                                else{
                                    details="";
                                }
                                final Earthquake earthquake=new Earthquake(idString, qdate, details, l, magnitude, linkString);
                                earthquakes.add(earthquake);
                            }
                        }
                    }
                    httpsConnection.disconnect();
                }
                catch (MalformedURLException e){
                    Log.e(TAG, "MalformedURLException", e);
                }
                catch (IOException e){
                    Log.e(TAG, "IOException", e);
                }
                catch (ParserConfigurationException e){
                    Log.e(TAG, "ParserConfigurationException", e);
                }
                catch (SAXException e){
                    Log.e(TAG, "SAXException", e);
                }
                EarthquakeDatabaseAccessor.getInstance(getApplication()).earthquakeDAO().insertEarthquakes(earthquakes);
                return earthquakes;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess(new Consumer<List<Earthquake>>() {
            @Override
            public void accept(List<Earthquake> data) throws Throwable {

            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {

            }
        }).subscribe();
    }
}
