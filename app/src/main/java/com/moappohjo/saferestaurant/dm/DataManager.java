package com.moappohjo.saferestaurant.dm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataManager {
    private Context context;
    Address address;

    public DataManager(Context context, Address address) {
        this.context = context;
        this.address = address;
    }

    public boolean loadData() {
        if (haveNetworkConnection(this.context)) {
            FetchItemTask ft = new FetchItemTask(this.context, this.address);
            ft.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //ft.execute();
        } else {

        }
        return true;
    }

    //네트워크와 통신하는 과정이므로 비동기 방식을 사용해야 함.
    private class FetchItemTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        Context context;
        Address address;

        public FetchItemTask(Context context, Address address) {
            super();
            this.context = context;
            this.address = address;
        }

        protected void onPreExecute() {
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
            myDBHelper dbHelper = new myDBHelper(this.context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //강제로 db의 table drop
            //dbHelper.onUpgrade(db, 39, 39);
            //db에서 restaurant data가 있는지 판별하고, 없다면 모두 집어넣는다.
            Cursor c = db.rawQuery("SELECT COUNT(*) FROM Restaurant", null);
            c.moveToFirst();
            if (c.getInt(0) == 0) {
                updateAllData(APIKEY, db);
            } else {
                Log.i("DB not empty", "success");
                //사용자의 위치를 구하고, 해당 시도의 정보 중 outdated 된 것이 있다면 해당 지역을 update한다.
                //ocation userLoc = getLocation(this.context);
                //System.out.println(getFromLocation(userLoc.getLatitude(), userLoc.getLongitude()));
            }

            return "Fail";
        }

        public void updateAllData(String APIKEY, SQLiteDatabase db) {
            String APIURL = "http://211.237.50.150:7080/openapi/" + APIKEY +
                    "/xml/Grid_20200713000000000605_1/1/1?&RELAX_USE_YN=Y";
            try {
                //우성 길이를 하나만 가져와서 총 몇 개의 안심식당이 있는지 확인합니다.
                Document result = Jsoup.connect(APIURL).method(Connection.Method.GET).execute().parse();
                int totalCnt = Integer.parseInt(result.select("totalCnt").text());

                //한 번의 요청 당 최대 1000개의 식당을 검색할 수 있습니다.
                //1000개씩 나눠 받은 결과를 result에 html형태로 붙여넣습니다.
                for (int startIndex = 1; startIndex <= totalCnt; startIndex += 1000) {
                    int endIndex = (startIndex + 999 < totalCnt) ? startIndex + 999 : totalCnt;
                    String partURL = "http://211.237.50.150:7080/openapi/" + APIKEY +
                            "/xml/Grid_20200713000000000605_1/" + startIndex + "/" + endIndex + "?&RELAX_USE_YN=Y";
                    result.append(Jsoup.connect(partURL).method(Connection.Method.GET).execute().parse().html());
                }

                //해당 도큐먼트 객체를 넘겨주고 DB에 삽입한다.
                InsertDB(db, result, totalCnt);

            } catch (IOException e) {
                Log.i("FAIL", "Exception occured");
                e.printStackTrace();
            }
        }

        public void updateSpecificRegion() {

        }

        public void InsertDB(SQLiteDatabase db, Document result, int totalCnt) {
//하나의 식당에 대한 정보는 다음과 같은 형식을 가집니다.
//                <row>
//                <ROW_NUM>1</ROW_NUM>
//                <RELAX_SEQ>2462</RELAX_SEQ>
//                <RELAX_ZIPCODE>420000</RELAX_ZIPCODE>
//                <RELAX_SI_NM>경기도</RELAX_SI_NM>
//                <RELAX_SIDO_NM>부천시</RELAX_SIDO_NM>
//                <RELAX_RSTRNT_NM>힛더스팟(현대중동점)</RELAX_RSTRNT_NM>
//                <RELAX_RSTRNT_REPRESENT>김주엽</RELAX_RSTRNT_REPRESENT>
//                <RELAX_ADD1>경기도 부천시 길주로 180</RELAX_ADD1>
//                <RELAX_ADD2>현대백화점 8층</RELAX_ADD2>
//                <RELAX_GUBUN>일반음식점</RELAX_GUBUN>
//                <RELAX_GUBUN_DETAIL>서양식</RELAX_GUBUN_DETAIL>
//                <RELAX_RSTRNT_TEL>032-623-2882</RELAX_RSTRNT_TEL>
//                <RELAX_USE_YN>Y</RELAX_USE_YN>
//                <RELAX_RSTRNT_ETC/>
//                </row>
//            db.execSQL("PRAGMA foreign_keys=ON");
//            db.execSQL("CREATE TABLE Restaurant (SEQ INTEGER PRIMARY KEY, ZIPCODE INTEGER, SI_NM TEXT, SIDO_NM TEXT," +
//                    "RSTRNT_NM TEXT, RSTRNT_REPRESENT TEXT, ADD1 TEXT, ADD2 TEXT, GUBUN TEXT, GUBUN_DETAIL TEXT, " +
//                    "RSTRNT_TEL TEXT, LATITUDE REAL, LONGITUDE REAL, LASTUPDATETIME TEXT);");
            // 현재시간을 msec 으로 구한다.
            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd");
            // nowDate 변수에 값을 저장한다.
            String currentTime = sdfNow.format(date);

            for (int idx = 0; idx < totalCnt; idx++) {
                Elements restaurantsInfo = result.select("row");
                Element element = restaurantsInfo.get(idx);
                int SEQ = Integer.parseInt(element.select("RELAX_SEQ").text());
                int ZIPCODE = Integer.parseInt(element.select("RELAX_ZIPCODE").text());
                String SI_NM = element.select("RELAX_SI_NM").text();
                String SIDO_NM = element.select("RELAX_SIDO_NM").text();
                String RSTRNT_NM = element.select("RELAX_RSTRNT_NM").text().replaceAll("'", "");
                String RSTRNT_REPRESENT = element.select("RELAX_RSTRNT_REPRESENT").text();
                String ADD1 = element.select("RELAX_ADD1").text().replaceAll("'", "");
                String ADD2 = element.select("RELAX_ADD2").text().replaceAll("'", "");
                String GUBUN = element.select("RELAX_GUBUN").text();
                String GUBUN_DETAIL = element.select("RELAX_GUBUN_DETAIL").text();
                String RSTRNT_TEL = element.select("RELAX_RSTRNT_TEL").text();
                //while(!haveNetworkConnection(this.context));
                try {
                    Address converted = getFromLocationName(ADD1);
                    double latitude;
                    double longitude;
                    if(converted!=null) {
                        latitude = converted.getLatitude();
                        longitude = converted.getLongitude();
                    }
                    else{
                        latitude= -1.0;
                        longitude= -1.0;
                    }
                    Log.i(idx + "", RSTRNT_NM);
                    db.execSQL("INSERT OR REPLACE INTO Restaurant VALUES(" + SEQ + ", " + ZIPCODE + ", '" + SI_NM + "', " +
                            "'" + SIDO_NM + "', '" + RSTRNT_NM + "', '" + RSTRNT_REPRESENT + "', '" + ADD1 + "', '" + ADD2 + "', " +
                            "'" + GUBUN + "', '" + GUBUN_DETAIL + "', '" + RSTRNT_TEL + "', " + latitude + ", " + longitude + ", '" + currentTime + "');");
                }catch (IOException e){
                    idx--;
                }
            }
        }

        public Address getFromLocationName(String addr) throws IOException{
            Geocoder g = new Geocoder(this.context);
            Address converted = null;
            //Log.i("address", addr);
            List<Address> buf = g.getFromLocationName(addr, 1);
            if(buf.size()!=0)
                converted = buf.get(0);
            else
                return null;
//            Log.i("gps", addresses.get(0).getLatitude()+ ", " + addresses.get(0).getLongitude());
            return converted;
        }

        public Address getFromLocation(double latitude, double longitude) {
            Geocoder g = new Geocoder(this.context);
            Address converted = null;
            try {
                converted = g.getFromLocation(latitude, longitude, 1).get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Log.i("gps", addresses.get(0).getLatitude()+ ", " + addresses.get(0).getLongitude());
            return converted;
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
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    protected Location getLocation(Context context) {
        final int GPS_ENABLE_REQUEST_CODE = 2001;
        final int PERMISSIONS_REQUEST_CODE = 100;
        String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, REQUIRED_PERMISSIONS[0])){
                ActivityCompat.requestPermissions((Activity)context, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions((Activity)context, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location;
    }
}


