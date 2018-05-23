package com.example.terence.server_communication;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText et;
    Button btn;
    TextView tv;

    //final String scripturlstring = "https://terence-thias.000webhostapp.com/test.php";
    final String scripturlstring = " https://terence-thias.000webhostapp.com/fangbuch/insert.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.editText);
        tv = (TextView) findViewById(R.id.textView);
        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetAvailable()) {
                    sendToServer(et.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Internet ist nicht Verfügbar.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
        public void sendToServer(final String text){

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        String textparam = URLEncoder.encode("vorname", "UTF-8")
                                + "=" + URLEncoder.encode(text, "UTF-8")+"&"+
                        URLEncoder.encode("nachname", "UTF-8")
                                + "=" + URLEncoder.encode("duuuu", "UTF-8");

                        URL scripturl = new URL(scripturlstring);
                        HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                        connection.setDoOutput(true);
                        //connection.setConnectTimeout(1000);
                        //connection.setRequestMethod("POST");

                       connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                        OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                        contentWriter.write(textparam);
                        contentWriter.flush();
                        contentWriter.close();

;


                        InputStream answerInputStream = connection.getInputStream();
                        final String  answer = getTextFromInputStream(answerInputStream);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(answer);
                            }
                        });
                        answerInputStream.close();
                        connection.disconnect();

                    } catch (MalformedURLException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
            }).start();

        }


        public String getTextFromInputStream(InputStream is){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();

            String aktuelleZeile;

            try {
                while((aktuelleZeile = reader.readLine()) != null ){
                    stringBuilder.append(aktuelleZeile);
                    stringBuilder.append("\n");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return stringBuilder.toString().trim();
        }


        public boolean internetAvailable(){
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =  connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    }

