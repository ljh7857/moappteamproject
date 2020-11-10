package com.moappohjo.saferestaurant.dm;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//DB를 생성하고 초기화하는 DB생성자 정의
public class myDBHelper extends SQLiteOpenHelper {
    public myDBHelper(Context context){
        super(context, "groupDB", null, 39);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
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
        db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL("CREATE TABLE Restaurant (SEQ INTEGER PRIMARY KEY, ZIPCODE INTEGER, SI_NM TEXT, SIDO_NM TEXT," +
                "RSTRNT_NM TEXT, RSTRNT_REPRESENT TEXT, ADD1 TEXT, ADD2 TEXT, GUBUN TEXT, GUBUN_DETAIL TEXT, " +
                "RSTRNT_TEL TEXT, USE_YN TEXT, LATITUDE REAL, LONGITUDE REAL, LASTUPDATETIME TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //이곳에선 테이블이 존재하면 없애고 새로 만들어준다.
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Subject");
        db.execSQL("DROP TABLE IF EXISTS OpenedClass");
        db.execSQL("DROP TABLE IF EXISTS LearnedClass");
        db.execSQL("DROP TABLE IF EXISTS Curriculum");
        onCreate(db);
    }
}