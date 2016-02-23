package campominado.com.jean.campominado;

/**
 * Created by Jean on 22/02/2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import campominado.com.jean.database.DBController;

public class Score extends Activity {
    public static final String KEY_SCORE = "campominado.com.jean.campominado.score";

    public EditText name;
    public Button save;

    public DBController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String score = getIntent().getStringExtra(KEY_SCORE);
        setContentView(R.layout.score);

        dbController = new DBController(this);

        name = (EditText) findViewById(R.id.score_name_edit);
        save = (Button) findViewById(R.id.score_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbController.criar(String.valueOf(name.getText()), score);
                finish();
            }
        });

    }
}
