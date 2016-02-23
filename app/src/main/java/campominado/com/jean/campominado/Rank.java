package campominado.com.jean.campominado;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import campominado.com.jean.database.DBController;
import campominado.com.jean.database.DBOpenHelper;

/**
 * Created by Jean on 23/02/2016.
 */
public class Rank extends AppCompatActivity {
    private ListView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank);
        loadListView();

    }

    public void loadListView() {
        results = (ListView) findViewById(R.id.rank_listview);
        DBController controller = new DBController(getBaseContext());

        final Cursor cursor = controller.atualizar();
        String[] campos = new String[] {DBOpenHelper.TABLE_ID, DBOpenHelper.PLAYER_NAME, DBOpenHelper.PLAYER_SCORE};
        int[] viewsID = new int[] {R.id.line_idTextView, R.id.line_nameTextView, R.id.line_scoresTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(), R.layout.rank_line, cursor, campos, viewsID, 0);
        results.setAdapter(adapter);
    }
}
