package com.example.celebsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsImage = new ArrayList<String>();
    ArrayList<String> celebsName =  new ArrayList<String>();
    ArrayList<String> buttonColl = new ArrayList<String>();
    Button btn1, btn2, btn3, btn4;
    ImageView imageView;
    int locationOfCurrAns;

    int choosenCelebImageIndex;

    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);


        //-----This method is use for download webpage content------
        DownloadText task = new DownloadText();

        String result = "" ;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] resultPart = result.split("<div class=\"sidebarContainer\">");

            celebsName.clear();
            celebsImage.clear();

            //------for Image of celebs
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(resultPart[0]);

            while(m.find()){

                celebsImage.add(m.group(1));

            }

            //------for name of celebs
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(resultPart[0]);

            while(m.find()){

                celebsName.add(m.group(1));

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //-----celeb choosen randomly-----//
        choosenCelebImageIndex = rand.nextInt(celebsImage.size());

        setNewCelebImage();
        setNewTextOnButtons();


    }


    public void listenToButton(View view){


        if(view.getTag().toString().equals(Integer.toString(locationOfCurrAns))){

            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

        }else{

            Toast.makeText(this, "wrong! It was "+celebsName.get(choosenCelebImageIndex), Toast.LENGTH_SHORT).show();
        }

        buttonColl.clear();

        //-----celeb choosen randomly-----//
        choosenCelebImageIndex = rand.nextInt(celebsImage.size());

        setNewCelebImage();

        setNewTextOnButtons();

    }


    public void setNewCelebImage(){

        Bitmap bitmapImg = null;

        DownloadImage taskImage = new DownloadImage();

        Log.i("Image No ", choosenCelebImageIndex+"");

        try {

            bitmapImg = taskImage.execute(celebsImage.get(choosenCelebImageIndex)).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bitmapImg);

    }






    public void setNewTextOnButtons(){

        //-----Celebs Name On Buttons-------
        Log.i("Choosen Celeb Name ", celebsName.get(choosenCelebImageIndex));

        locationOfCurrAns = rand.nextInt(4);

        int wrongAns = rand.nextInt(celebsName.size());

        for(int i=0; i<4; i++){

            if(locationOfCurrAns == i){
                buttonColl.add(celebsName.get(choosenCelebImageIndex));
            }else {

                while(wrongAns == choosenCelebImageIndex){
                    wrongAns = rand.nextInt(celebsName.size());
                }

                buttonColl.add(celebsName.get(wrongAns));

                wrongAns = rand.nextInt(celebsName.size());
            }
        }

        //--- setting text on the button -------//
        btn1.setText(buttonColl.get(0));
        btn2.setText(buttonColl.get(1));
        btn3.setText(buttonColl.get(2));
        btn4.setText(buttonColl.get(3));
    }






    public class DownloadText extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection connection;

            try {

                url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {

                e.printStackTrace();
                return null;

            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }

        }
    }


    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap = null;
            URL url;
            HttpURLConnection connection;

            try {

                url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream in = connection.getInputStream();

                bitmap = BitmapFactory.decodeStream(in);

                return bitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();
                return null;

            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }

        }
    }




}
