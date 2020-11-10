package com.moappohjo.saferestaurant.dm;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataManager {
    private Context context;
    Address address;
    public DataManager(Context context, Address address){
        this.context = context;
        this.address = address;
    }
    public boolean loadData(){
        if(haveNetworkConnection(this.context)){
            FetchItemTask ft = new FetchItemTask(this.context, this.address);
            ft.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{

        }
        return true;
    }

    //네트워크와 통신하는 과정이므로 비동기 방식을 사용해야 함.
    private class FetchItemTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        Context context;
        Address address;
        public FetchItemTask(Context context, Address address){
            super();
            this.context = context;
            this.address = address;
        }

        protected void onPreExecute(){
//            progressDialog = new ProgressDialog(this.context);
//            progressDialog.setMessage("안심식당 정보 로딩중...");
//            progressDialog.setCancelable(false);
//            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
//            progressDialog.show();
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
                //1000개씩 나눠 받은 결과를 result에 html형태로 붙여넣습니다.
                for(int startIndex=1; startIndex<=totalCnt; startIndex+=1000){
                    int endIndex = (startIndex+999<totalCnt)? startIndex+999: totalCnt;
                    String partURL = "http://211.237.50.150:7080/openapi/"+ APIKEY +
                            "/xml/Grid_20200713000000000605_1/"+startIndex+"/"+endIndex+"?&RELAX_USE_YN=Y";
                    result.append(Jsoup.connect(partURL).method(Connection.Method.GET).execute().parse().html());
                }

//                for(int idx=0; idx<totalCnt; idx++){
//                    Elements restaurantsInfo = result.select("row");
//                    Element element = restaurantsInfo.get(idx);
//                    Log.i(idx+"",element.select("RELAX_RSTRNT_NM").text());
////                        Log.i(idx+"",element.select("ROW_NUM").text());
//                    String restaurantName = element.select("RELAX_RSTRNT_NM").text().replaceAll("'", "-");
//                }
                Geocoder g = new Geocoder(this.context);
                List<Address> addresses = null;
                String addr = result.select("row").get(0).select("RELAX_ADD1").text();
                Log.i("address", addr);
                addresses = g.getFromLocationName(addr, 1);
                Log.i("gps", addresses.get(0).getLatitude()+ ", " + addresses.get(0).getLongitude());
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
        }
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
}


