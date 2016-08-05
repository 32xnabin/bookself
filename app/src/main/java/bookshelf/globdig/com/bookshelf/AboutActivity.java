package bookshelf.globdig.com.bookshelf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    ImageView back;
    TextView backText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backText=(TextView)findViewById(R.id.backText);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        WebView wb=(WebView)findViewById(R.id.aboutWeb);

        wb.loadData("<p style='text-align:justify'>Travel News er en uavhengig og kritisk formidler av informasjon og kunnskap for alle deler av reiselivet. Vi konsentrerer oss om næringslivsjournalistikk og følger betalingsstrømmene i bransjen. Travel News formidler stoff om reisebyråer, fly, hotell, charter og turoperatører, rederier, leiebil og alt annet som har sin naturlige plass i reisebransjen.</p>", "text/html", "UTF-8");
    }
}
