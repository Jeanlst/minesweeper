package campominado.com.jean.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import campominado.com.jean.campominado.myDatabase;

/**
 * Created by Joaquim Nabor on 22/02/2016.
 */

public class DBController implements myDatabase {

    private SQLiteDatabase database;
    private DBOpenHelper openHelper;

    public DBController(Context context){
        openHelper = new DBOpenHelper(context);
    }

    @Override
    public String criar(String nome, String pontuacao) {
        database = openHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(openHelper.PLAYER_NAME, nome);
        valores.put(openHelper.PLAYER_SCORE, pontuacao);

        long status = database.insert(openHelper.TABLE_NAME, null, valores);

        if (status == -1) {
            return "Ocorreu um erro.";
        }
        return "Pontuação adicionada com sucesso!";
    }

    @Override
    public Cursor atualizar() {
        database = openHelper.getReadableDatabase();
        String[] colunas = {openHelper.TABLE_ID, openHelper.PLAYER_NAME, openHelper.PLAYER_SCORE};

        Cursor cursor = database.query(openHelper.TABLE_NAME, colunas, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    @Override
    public Cursor carregarID(int id) {
        database = openHelper.getReadableDatabase();
        String[] colunas = {openHelper.PLAYER_NAME, openHelper.PLAYER_SCORE};
        String argumento = openHelper.TABLE_ID +"="+ id;

        Cursor cursor = database.query(openHelper.TABLE_NAME, colunas, argumento, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    @Override
    public void editar(int id, String nome, String pontuacao) {
        database = openHelper.getWritableDatabase();
        String argumento = openHelper.TABLE_ID +"="+ id;
        ContentValues valores = new ContentValues();

        valores.put(openHelper.PLAYER_NAME, nome);
        valores.put(openHelper.PLAYER_SCORE, pontuacao);

        database.update(openHelper.TABLE_NAME, valores, argumento, null);
    }

    @Override
    public void deletar(int id) {
        database = openHelper.getReadableDatabase();
        String argumento = openHelper.TABLE_ID +"="+ id;

        database.delete(openHelper.TABLE_NAME, argumento, null);

    }
}
