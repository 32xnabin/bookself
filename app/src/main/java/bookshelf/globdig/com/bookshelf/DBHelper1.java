package bookshelf.globdig.com.bookshelf;

/**
 * Created by Sandip on 02/17/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DBHelper1 extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bookshelf.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_STREET = "street";
    public static final String CONTACTS_COLUMN_CITY = "place";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    private HashMap hp;


    public static final String FILTER_TABLE_NAME = "filter";
    public static final String FILTER_COLUMN_ID = "id";
    public static final String FILTER_COLUMN_MAG_ID = "mag_id";
    public static final String FILTER_COLUMN_IMAGE = "image";
    public static final String FILTER_COLUMN_HEADING = "heading";
    public static final String FILTER_COLUMN_CONTENT = "content";



    public static final String FILTER_CREATE = "create table filter(id integer primary key, mag_id text,page_number text,heading text,content text)";
    public static final String PHOTO_CREATE = "create table image(id integer primary key, mag_id text,image blob)";
    public static final String Details_CREATE = "create table detail(id integer primary key, mag_id text,name text,introduction text,article text,issue_date text)";

    public static final String Details_All = "create table details(id integer primary key, mag_id text,name text,intro text,article text,issue_date text,image blob,image_url text,content,zip_file text,subscribed text,is_free text)";




    Context context;
    public DBHelper1(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(
//                "create table filter(id integer primary key, mag_id integer,image blob,heading text,content text)"
//        );
//        db.execSQL(FILTER_CREATE);
//        db.execSQL(PHOTO_CREATE);
//        db.execSQL(Details_CREATE);
        db.execSQL(Details_All);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
//        db.execSQL("DROP TABLE IF EXISTS filter");
//        db.execSQL("DROP TABLE IF EXISTS image");
        db.execSQL("DROP TABLE IF EXISTS details");
        onCreate(db);
    }


    public void deleteDetails()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM  details");
        db.close();
    }

    public long insertAllDetails1(String mag_id, String name,String intro,String article,String issue_date, byte[] image,String image_url,String content,String zip_file,String subscribed,String isFree)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("mag_id",mag_id);
        initialValues.put("name", name);
        initialValues.put("intro", intro);
        initialValues.put("article", article);
        initialValues.put("issue_date", issue_date);
       // initialValues.put("image", image);
        initialValues.put("image_url", image_url);
        initialValues.put("content", content);
        initialValues.put("zip_file", zip_file);
        initialValues.put("subscribed", subscribed);
        initialValues.put("is_free", isFree);

        return db.insert("details", null, initialValues);
    }


    public long insertAllDetails(String mag_id, String name,String intro,String article,String issue_date, byte[] image,String image_url,String content,String zip_file,String subscribed,String isFree)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("mag_id",mag_id);
        initialValues.put("name", name);
        initialValues.put("intro", intro);
        initialValues.put("article", article);
        initialValues.put("issue_date", issue_date);
        // initialValues.put("image", image);
        initialValues.put("image_url", image_url);
        initialValues.put("content", content);
        initialValues.put("zip_file", zip_file);
        initialValues.put("subscribed", subscribed);
        initialValues.put("is_free", isFree);

      // return db.update("details", initialValues, "mag_id= ? ", new String[]{mag_id});

        return  db.insertWithOnConflict("details","mag_id", initialValues,
                SQLiteDatabase.CONFLICT_REPLACE);

        // return db.insert("details", null, initialValues);
    }

    public ArrayList<AllMagazineFilterObject> search(String searchString)
    {
        ArrayList<AllMagazineFilterObject> array_list = new ArrayList<AllMagazineFilterObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        String  sea=searchString.toUpperCase();
        Cursor res =  db.rawQuery( "select * from details where  UPPER(content) like  "+"'%"+sea+"%' order by mag_id desc", null );
        System.out.println("counttttttttttttttttttttttttttttttttttttttt---->" + res.getCount());
        res.moveToFirst();
        while(res.isAfterLast() == false)
        {
            AllMagazineFilterObject obj=new AllMagazineFilterObject();
            System.out.println("subscribed--dbbbb-->"+res.getString(res.getColumnIndex("subscribed")));
            System.out.println("is_free--dbbb-->" + res.getString(res.getColumnIndex("is_free")));
            obj.setId(res.getString(res.getColumnIndex("mag_id")));
            obj.setContent(res.getString(res.getColumnIndex("content")));
            obj.setImage_url(res.getString(res.getColumnIndex("image_url")));
            obj.setZip_file(res.getString(res.getColumnIndex("zip_file")));
            obj.setSubscribed(res.getString(res.getColumnIndex("subscribed")));
            obj.setIsFree(res.getString(res.getColumnIndex("is_free")));

            array_list.add(obj);
            res.moveToNext();



        }
           return  array_list;
    }
    public ArrayList<AllMagazineFilterObject> getAll(int state)
    {
        ArrayList<AllMagazineFilterObject> array_list = new ArrayList<AllMagazineFilterObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from details order by mag_id desc", null );

        res.moveToFirst();
        while(res.isAfterLast() == false){



            AllMagazineFilterObject obj=new AllMagazineFilterObject();
            System.out.println("mag_id--dbbbb-->"+res.getString(res.getColumnIndex("mag_id")));
            System.out.println("content--dbbb-->" + res.getString(res.getColumnIndex("content")));
            obj.setId(res.getString(res.getColumnIndex("mag_id")));
            obj.setContent(res.getString(res.getColumnIndex("content")));
            obj.setImage_url(res.getString(res.getColumnIndex("image_url")));
            obj.setArticle(res.getString(res.getColumnIndex("article")));
            obj.setIssue_date(res.getString(res.getColumnIndex("issue_date")));
            obj.setZip_file(res.getString(res.getColumnIndex("zip_file")));
            obj.setSubscribed(res.getString(res.getColumnIndex("subscribed")));
            obj.setIsFree(res.getString(res.getColumnIndex("is_free")));

            SharedPreferences sf=context.getSharedPreferences("all", 0);

                        if(state==1){
                            if(!sf.getString(obj.getId(),"no").equals("no")) {
                                array_list.add(obj);
                            }


                        }
                        else if(state==2){
                            String favString=sf.getString("favlist", ",");
                            String[]favArray=favString.split(",");
                            if(Arrays.asList(favArray).contains(obj.getId())){
                                array_list.add(obj);
                            }


                        }
                     else{
                           // System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhh---Contentttttttttttttt---->" +obj.getContent());
                            array_list.add(obj);
                        }



            res.moveToNext();



        }
        System.out.println("counttttttttttttt+array_list.size()---->" + array_list.size());
        return  array_list;
    }



    public ArrayList<String> getAllDetailsByText(String searchString)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from details  having content like"+"'%"+searchString+"%'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public long insertImage(String mag_id, byte[] photo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("mag_id", 1);
        initialValues.put("image", photo);
        return db.insert("image", null, initialValues);
    }
    public long insertFilter(String mag_id, String heading,String content,String page_number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("mag_id",mag_id);
        initialValues.put("page_number",page_number);
        initialValues.put("heading",heading);
        initialValues.put("content",content);

        return db.insert("filter", null, initialValues);
    }
    public long insertDetails(String mag_id, String name,String intro,String article,String issue_date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("mag_id",mag_id);
        initialValues.put("name",name);
        initialValues.put("intro",intro);
        initialValues.put("article",article);
        initialValues.put("issue_date",issue_date);

        return db.insert("detail", null, initialValues);
    }



    public byte[] getImageByMagId(String mag_id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from image where mag_id="+mag_id, null );
        res.moveToFirst();
        byte[] photo=null;

        while(res.moveToNext())
        {
           photo=res.getBlob(res.getColumnIndex("image"));

        }

        return  photo;
    }




    public ArrayList<PagesObject> getAllFilteredPages(String searchString)
    {
        ArrayList<PagesObject> array_list = new ArrayList<PagesObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from filter where heading like"+"'%"+searchString+"%'  or "+"content like"+"'%"+searchString+"%' order by mag_id", null );
        res.moveToFirst();
        while(res.moveToNext())
        {
            PagesObject obj=new PagesObject();
            obj.setPage_number(res.getString(res.getColumnIndex("page_number")));
            obj.setHeading(res.getString(res.getColumnIndex("heading")));
            obj.setMagazine_id(res.getString(res.getColumnIndex("mag_id")));
            array_list.add(obj);


        }
        return array_list;
    }


        public ArrayList<String> getAllFilter(String searchString)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from filter where heading like"+"'%"+searchString+"%'  or "+"content like"+"'%"+searchString+"%'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }



//    public boolean insertContact  (String name, String phone, String email, String street,String place)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.insert("contacts", null, contentValues);
//        return true;
//    }
//
//    public Cursor getData(int id){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
//        return res;
//    }
//
//    public int numberOfRows(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
//        return numRows;
//    }
//
//    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
//        return true;
//    }
//
//    public Integer deleteContact (Integer id)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete("contacts",
//                "id = ? ",
//                new String[] { Integer.toString(id) });
//    }
//
//    public ArrayList<String> getAllCotacts()
//    {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from contacts", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//            res.moveToNext();
//        }
//        return array_list;
//    }
}
