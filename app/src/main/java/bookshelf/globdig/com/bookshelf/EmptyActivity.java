package bookshelf.globdig.com.bookshelf;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Dialog dialog = new Dialog (this);

        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.dialog_progress);
        dialog.getWindow ().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
}
