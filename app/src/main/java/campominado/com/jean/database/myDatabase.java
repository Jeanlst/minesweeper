package campominado.com.jean.campominado;

import android.database.Cursor;

/**
 * Created by Joaquim Nabor on 22/02/2016.
 */
public interface myDatabase {

    public String criar(String nome, String pontuacao);
    public Cursor atualizar();
    public Cursor carregarID(int id);
    public void editar(int id, String nome, String pontuacao);
    public void deletar(int id);
}
