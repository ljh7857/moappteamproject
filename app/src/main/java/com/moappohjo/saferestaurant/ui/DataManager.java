package com.moappohjo.saferestaurant.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DataManager {
    private Context context;
    public DataManager(Context context){
        this.context = context;
    }
    public boolean loadData(){
        if(haveNetworkConnection(this.context)){
            //비동기 클래스를 사용해 사용자 관련 정보들을 가져온다.
            FetchItemTask ft = new FetchItemTask(this.context);
            ft.execute();
        }
        else{

        }
        return true;
    }

    protected boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                haveConnectedWifi = ni.isConnected();
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                haveConnectedMobile = ni.isConnected();
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //네트워크와 통신하는 과정이므로 비동기 방식을 사용해야 함.
    private class FetchItemTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        Context context;
        public FetchItemTask(Context context){
            super();
            this.context = context;
        }

        protected void onPreExecute(){
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage("안심식당 정보 로딩중...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progressDialog.show();
        }

        //백그라운드에서 실행하는 내용
        @Override
        protected String doInBackground(Void... voids) {
            String APIKEY = "0f8513fb24b87da71f5eb1594e0ac11b35b2be4afe6c06a1c543dcd9169a376f";
            String APIURL = "http://211.237.50.150:7080/openapi/"+ APIKEY +
                    "/xml/Grid_20200713000000000605_1/1/1?&RELAX_USE_YN=Y";
            //현재는 실행마다 사이트에 접속해서 업데이트를 하는 구조인데, 이후 주기적으로 업데이트하도록 수정 예정임.
            myDBHelper dbHelper = new myDBHelper(this.context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                //우성 길이를 하나만 가져와서 총 몇 개의 안심식당이 있는지 확인합니다.
                Document result = Jsoup.connect(APIURL).method(Connection.Method.GET).execute().parse();
                int totalCnt = Integer.parseInt(result.select("totalCnt").text());

                //한 번의 요청 당 최대 1000개의 식당을 검색할 수 있습니다.
                for(int startIndex=1; startIndex<=totalCnt; startIndex+=1000){
                    int endIndex = (startIndex+999<totalCnt)? startIndex+999: totalCnt;
                    String partURL = "http://211.237.50.150:7080/openapi/"+ APIKEY +
                            "/xml/Grid_20200713000000000605_1/"+startIndex+"/"+endIndex+"?&RELAX_USE_YN=Y";
                    //System.out.println(partURL);
                    result = Jsoup.connect(partURL).method(Connection.Method.GET).execute().parse();
                    Elements restaurantsInfo = result.select("row");
                    //System.out.println(restaurantsInfo.size());
                    for(int idx=0; idx<endIndex-startIndex; idx++){
                        Element element = restaurantsInfo.get(idx);
                        Log.i(startIndex+idx+"",element.select("RELAX_RSTRNT_NM").text());
//                        Log.i(idx+"",element.select("ROW_NUM").text());
                        String restaurantName = element.select("RELAX_RSTRNT_NM").text().replaceAll("'", "-");
//                        db.execSQL("INSERT OR REPLACE INTO Restaurant VALUES("+element.select("RELAX_SEQ").text()+", "+element.select("RELAX_ZIPCODE").text()+", " +
//                                "'"+element.select("RELAX_SI_NM").text()+"', '"+element.select("RELAX_SIDO_NM").text()+"', '"+restaurantName+"', " +
//                                "'"+element.select("RELAX_RSTRNT_REPRESENT").text()+"', '"+element.select("RELAX_ADD1").text().replaceAll("'", "")+"', '"+element.select("RELAX_ADD2").text()+"', " +
//                                "'"+element.select("RELAX_GUBUN").text()+"', '"+element.select("RELAX_GUBUN_DETAIL").text()+"', '"+element.select("RELAX_RSTRNT_TEL").text()+"', " +
//                                "'"+element.select("RELAX_RSTRNT_ETC").text()+"');");
                    }
                }
            } catch (IOException e) {
                Log.i("FAIL", "Exception occured");
                e.printStackTrace();
            }
            return "Fail";
        }

        //포그라운드에서 실행하는 내용
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }
}


