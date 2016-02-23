package campominado.com.jean.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joaquim Nabor on 21/02/2016.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "score.db";
    protected static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Rank";
    public static final String TABLE_ID = "_id";
    public static final String PLAYER_NAME = "Nome";
    public static final String PLAYER_SCORE = "Pontuacao";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_criarBanco = "CREATE TABLE "+TABLE_NAME+" ("+TABLE_ID+" integer primary key autoincrement, "+ PLAYER_NAME +" text, "+ PLAYER_SCORE +" text)";

        db.execSQL(sql_criarBanco);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql_atualizarBanco = "DROP TABLE IF EXISTS "+ TABLE_NAME;

        db.execSQL(sql_atualizarBanco);

        onCreate(db);
    }
}
