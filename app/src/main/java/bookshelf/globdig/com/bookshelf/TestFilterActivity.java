package bookshelf.globdig.com.bookshelf;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestFilterActivity extends AppCompatActivity {


    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_filter);
        img=(ImageView)findViewById(R.id.testImage);

          convert();
       // forDataParsing();
    }


    public void convert() {


        String testString="" +
                "Heading1!!!Content1 dsfsdfdsfsdfsfd |||" +
                "Heading2!!!Content2 sfjsldfjsdlkfjlsdkjfl sdfjfsldsfj|||" +
                "Heading3!!!Content3 sdlfjslfjsldjfldsf |||" +
                "Heading4!!!Content4 sdlfjsvvvldjfldsf |||" +
                "Heading5!!!Content5 sdlfjslfjsldjfldsf |||" +
                "Heading6!!!Content6 sdlfjslnnnldjfldsf |||" +
                "Heading7!!!Content7 sdlfjslfjsldjfldsf |||" +
                "Heading8!!!Content8 sdlfjslfjsldjfldsf |||" +
                "Heading9!!! Content9 sdfklsdjflskdjfsdjflkdsjflkjds|||";

        String testString2="" +
                "Heading1!!!Content1 dsfsdfdsfsdfsfd |||" +
                "Heading2!!!Content2 sfjsldfjsdlkfjlsdkjfl sdfjfsldsfj|||" +
                "Heading3!!!Content3 sdlfjsyyysldjfldsf |||" +
                "Heading4!!!Content4 sdlfjslfjsldjfldsf |||" +
                "Heading5!!!Content5 sdlfjsljjjldjfldsf |||" +
                "Heading6!!!Content6 sdlfjslfjsldjfldsf |||" +
                "Heading7!!!Content7 sdlfjsluuudjfldsf |||" +
                "Heading8!!!Content8 sdlfjsmmldjfldsf |||" +
                "Heading9!!! Content9 sdfklsdjflskdjfsdjflkdsjflkjds|||";

        String testString3="" +
                "Heading1!!!Content1 dsfsdfdsfsdfsfd |||" +
                "Heading2!!!Content2 sfjsldfjsdlkfjlsdkjfl sdfjfsldsfj|||" +
                "Heading3!!!Content3 sdlfjslfjsldjfldsf |||" +
                "Heading4!!!Content4 sdlfjslfooojfldsf |||" +
                "Heading5!!!Content5 sdlfjslfjsldjfldsf |||" +
                "Heading6!!!Content6 sdlwwfjsldjfldsf |||" +
                "Heading7!!!Content7 sdlfjslfjsldjfldsf |||" +
                "Heading8!!!Content8 sdlfjslfjsldjfldsf |||" +
                "Heading9!!! Content9 sdfklzzzzzzjflskdjfsdjflkdsjflkjds|||";

            String []allPages=new String[3];

        allPages[0]=testString;
        allPages[1]=testString2;
        allPages[2]=testString3;

        for(int j=0;j<allPages.length;j++){


            String[] page=allPages[j].split("\\|\\|\\|");

            List<PagesObject> pagesList=new ArrayList<PagesObject>();

            for(int i=0;i<page.length;i++){
                PagesObject obj=new PagesObject();
                String[] p=page[i].split("!!!");

                obj.setHeading(p[0]);
                obj.setContent(p[1]);
                pagesList.add(obj);
            }
            for(PagesObject obj:pagesList){

            }
        }





      //  SQLiteDatabase db = this.getWritableDatabase();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable)this.getResources().getDrawable(R.drawable.ic_logout)).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();

        // second
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        Bitmap bitmap1 = ((BitmapDrawable)this.getResources().getDrawable(R.drawable.backf_arrow)).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos1);
        byte[] photo1 = baos1.toByteArray();

        // second
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        Bitmap bitmap2 = ((BitmapDrawable)this.getResources().getDrawable(R.drawable.media_logo)).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos2);
        byte[] photo2 = baos2.toByteArray();

        DBHelper helper=new DBHelper(this);
        try {
//            helper.insertAllDetails("1","magazine 1","intro 1","article 1","date 1",photo,testString);
//            helper.insertAllDetails("2","magazine 2","intro 2","article 2","date 2",photo1,testString2);
//            helper.insertAllDetails("3","magazine 3","intro 3","article 3","date 3",photo2,testString3);
            helper.search("zz");
        }catch (Exception e){
            e.printStackTrace();
        }


//        byte[] test=helper.getImage();
//        ByteArrayInputStream imageStream = new ByteArrayInputStream(test);
//        Bitmap theImage= BitmapFactory.decodeStream(imageStream);
//
//        img.setImageBitmap(theImage);


    }

    public void forDataParsing(){

        LinearLayout options_layout = (LinearLayout) findViewById(R.id.dynamic_layout);
        LinearLayout options=new LinearLayout(this);

        int count=0;
        for (int i = 0; i < 80; i++) {


            if(count%6==0||i==0){  options=new LinearLayout(this);}
//            View to_add = inflater.inflate(R.layout.options_element,
//                    options_layout,false);

            TextView text = new TextView(this);
            text.setText(i + " " + "hello");


            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView text = (TextView) v;
                    String trimmed = text.getText().toString().substring(0, text.getText().toString().indexOf(" "));


                    Toast.makeText(TestFilterActivity.this, "test000--" + trimmed, Toast.LENGTH_LONG).show();
                }
            });
            options.addView(text);

            if(count%6==0){ options_layout.addView(options);}


            count++;
        }


        String testString="" +
                "Heading1!!!Content1 dsfsdfdsfsdfsfd |||" +
                "Heading2!!!Content2 sfjsldfjsdlkfjlsdkjfl sdfjfsldsfj|||" +
                "Heading3!!!Content3 sdlfjslfjsldjfldsf |||" +
                "Heading4!!!Content4 sdlfjslfjsldjfldsf |||" +
                "Heading5!!!Content5 sdlfjslfjsldjfldsf |||" +
                "Heading6!!!Content6 sdlfjslfjsldjfldsf |||" +
                "Heading7!!!Content7 sdlfjslfjsldjfldsf |||" +
                "Heading8!!!Content8 sdlfjslfjsldjfldsf |||" +
                "Heading9!!! Content9 sdfklsdjflskdjfsdjflkdsjflkjds|||";

        String testString2="" +
                "Heading1!!!Content1 dsfsdfdsfsdfsfd |||" +
                "Heading2!!!Content2 sfjsldfjsdlkfjlsdkjfl sdfjfsldsfj|||" +
                "Heading3!!!Content3 sdlfjslfjsldjfldsf |||" +
                "Heading4!!!Content4 sdlfjslfjsldjfldsf |||" +
                "Heading5!!!Content5 sdlfjslfjsldjfldsf |||" +
                "Heading6!!!Content6 sdlfjslfjsldjfldsf |||" +
                "Heading7!!!Content7 sdlfjslfjsldjfldsf |||" +
                "Heading8!!!Content8 sdlfjslfjsldjfldsf |||" +
                "Heading9!!! Content9 sdfklsdjflskdjfsdjflkdsjflkjds|||";

        String testString3="" +
                "Heading1!!!Content1 dsfsdfdsfsdfsfd |||" +
                "Heading2!!!Content2 sfjsldfjsdlkfjlsdkjfl sdfjfsldsfj|||" +
                "Heading3!!!Content3 sdlfjslfjsldjfldsf |||" +
                "Heading4!!!Content4 sdlfjslfjsldjfldsf |||" +
                "Heading5!!!Content5 sdlfjslfjsldjfldsf |||" +
                "Heading6!!!Content6 sdlfjslfjsldjfldsf |||" +
                "Heading7!!!Content7 sdlfjslfjsldjfldsf |||" +
                "Heading8!!!Content8 sdlfjslfjsldjfldsf |||" +
                "Heading9!!! Content9 sdfklsdjflskdjfsdjflkdsjflkjds|||";

        String[] page=testString.split("\\|\\|\\|");
        System.out.println("pages length--->"+page.length);
        System.out.println("pages--->"+page[0]);
        List<PagesObject> pagesList=new ArrayList<PagesObject>();

        for(int i=0;i<page.length;i++){
            PagesObject obj=new PagesObject();
            String[] p=page[i].split("!!!");
            System.out.println("p length--->"+p.length);
            System.out.println("p--->"+p[0]);
            obj.setHeading(p[0]);
            obj.setContent(p[1]);
            pagesList.add(obj);
        }
        for(PagesObject obj:pagesList){
            System.out.println("Heading--->"+obj.getHeading());
            System.out.println("Content--->"+obj.getContent());
        }
    }
}
