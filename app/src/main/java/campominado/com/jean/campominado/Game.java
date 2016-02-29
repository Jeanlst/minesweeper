package campominado.com.jean.campominado;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import java.util.Random;
import android.os.Handler;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Jean on 21/02/2016.
 */
public class Game extends Activity {
    public static final String KEY_DIFFICULTY = "campominado.com.jean.campominado.difficulty";
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;
    public int diff;

    public int totalRows;
    public int totalCols;
    public int totalMines;

    public final int easyRows = 9;
    public final int mediumRows = 16;
    public final int hardRows = 30;

    public final int easyColumns = 9;
    public final int mediumColumns = 16;
    public final int hardColumns = 16;

    public final int easyMines = 10;
    public final int mediumMines = 40;
    public final int hardMines = 99;

    public boolean timerStarted;
    public boolean minesSet;

    public Tile[][] tiles;

    public Handler timer;
    public int secondsPassed;

    public TextView timerText;
    public TextView minesText;
    public TableLayout mineField;
    public TableLayout tableLayout;

    public int minesCount;
    public int flagsCount;
    public int correctFlags;
    public int totalCoveredTiles;

    public ImageButton imageButton;

    public int tileWH = 60;
    public int tilePadding = 5;

    public int mineFieldldWidth;
    public int mineFieldHeight;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        setContentView(R.layout.game);
        timerStarted = false;
        minesSet = false;
        timer = new Handler();
        secondsPassed = 0;
        timerText = (TextView) findViewById(R.id.Timer);
        minesText = (TextView) findViewById(R.id.MineCount);
        mineField = (TableLayout) findViewById(R.id.MineField);
        mineFieldHeight = mineField.getHeight();
        mineFieldldWidth = mineField.getWidth();

        imageButton = (ImageButton) findViewById(R.id.Smiley);
        imageButton.setBackgroundResource(R.drawable.smile);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                restartGame();
                Intent intent = new Intent(Game.this, Score.class);
                intent.putExtra(Score.KEY_SCORE, "" + secondsPassed);
                startActivity(intent);
            }
        });

        createGameBoard(diff);
        setupMineField(totalRows, totalCols);
        showGameBoard();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void createGameBoard(int diff) {
        //set o total de linhas e colunas baseado na dificuldade
        totalRows = easyRows;
        totalCols = easyColumns;
        totalMines = easyMines;
        switch (diff) {
            case DIFFICULTY_EASY:
                break;
            case DIFFICULTY_MEDIUM:
                totalRows = mediumRows;
                totalCols = mediumColumns;
                totalMines = mediumMines;
                break;
            case DIFFICULTY_HARD:
                totalRows = hardRows;
                totalCols = hardColumns;
                totalMines = hardMines;
                break;
        }

        //inicia a matris de tiles
        tiles = new Tile[totalRows][totalCols];
        minesCount = totalMines;
        flagsCount = 0;
        correctFlags = 0;
        totalCoveredTiles = totalRows * totalCols;
        setMinesText();

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                //cria um tile
                tiles[row][col] = new Tile(this);
                //seta o padrao do tile
                tiles[row][col].setDefaults();

                final int curRow = row;
                final int curCol = col;

                //adiciona um click listener
                tiles[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MediaPlayer media = MediaPlayer.create(getApplicationContext(), R.raw.click);
                        media.start();

                        if (!timerStarted) {
                            startTimer();
                            timerStarted = true;
                        }
                        if (!minesSet) {
                            minesSet = true;
                        }

                        if (!tiles[curRow][curCol].isFlag()) {
                            if (tiles[curRow][curCol].isMine()) {
                                tiles[curRow][curCol].openTile();
                                loseGame();
                            } else
                                uncoverTiles(curRow, curCol);

                            if (checkWonGame())
                                winGame();
                        }
                    }
                });

                //adiciona um long click listener
                tiles[row][col].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        MediaPlayer media = MediaPlayer.create(getApplicationContext(), R.raw.click);
                        media.start();
                        if (!timerStarted) {
                            startTimer();
                            timerStarted = true;
                        }
                        if (!minesSet) {
                            minesSet = true;
                        }

                        if (tiles[curRow][curCol].isCovered()) {
                            if (tiles[curRow][curCol].isQuestionMark()) {
                                tiles[curRow][curCol].setQuestionMark(false);
                                tiles[curRow][curCol].setCoveredIcon();
                            } else if (!tiles[curRow][curCol].isFlag()) {
                                tiles[curRow][curCol].setFlag(true);
                                tiles[curRow][curCol].setFlagIcon();

                                if (tiles[curRow][curCol].isMine())
                                    correctFlags++;

                                minesCount--;
                                flagsCount++;
                                setMinesText();
                            } else if (tiles[curRow][curCol].isFlag()) {
                                tiles[curRow][curCol].setFlag(false);
                                tiles[curRow][curCol].setQuestionMark(true);
                                tiles[curRow][curCol].setQuestionMarkIcon();

                                if (tiles[curRow][curCol].isMine())
                                    correctFlags--;

                                minesCount++;
                                flagsCount--;
                                setMinesText();
                            }
                        }

                        if (checkWonGame() && minesCount == 0)
                            winGame();

                        return true;
                    }
                });
            }
        }

    }

    public void setupMineField(int row, int col) {
        Random random = new Random();
        int mineRow;
        int mineCol;
        for (int i = 0; i < totalMines; i++) {
            mineRow = random.nextInt(totalRows);
            mineCol = random.nextInt(totalCols);

            if (mineRow == row && mineCol == col) { //tile clicado
                i--;
            } else if (tiles[mineRow][mineCol].isMine()) { //ja eh uma mina
                i--;
            } else {
                //planta uma nova mina
                tiles[mineRow][mineCol].plantMine();
                // uma linha e coluna para tras
                int startRow = mineRow - 1;
                int startCol = mineCol - 1;
                //checa 3 para tras e 3 para frente
                int checkRows = 3;
                int checkCols = 3;
                if (startRow < 0) { //se estiver na primeira linha
                    startRow = 0;
                    checkRows = 2;
                } else if (startRow + 3 > totalRows) //se estiver na ultima linha
                    checkRows = 2;

                if (startCol < 0) {
                    startCol = 0;
                    checkCols = 2;
                } else if (startCol + 3 > totalCols) //se estivar na ultima coluna
                    checkCols = 2;

                for (int j = startRow; j < startRow + checkRows; j++) {//3 linhas para frente
                    for (int k = startCol; k < startCol + checkCols; k++) {//3 linhas para baixo
                        if (!tiles[j][k].isMine()) //se nao for uma mina
                            tiles[j][k].updateSurroundingMineCount();
                    }
                }
            }
        }
    }

    public void showGameBoard() {
        //para cada linha
        for (int row = 0; row < totalRows; row++) {
            //cria uma nova table row
            TableRow tableRow = new TableRow(this);
            //seta a largura e altura da tablerow
            tableRow.setLayoutParams(new LayoutParams((tileWH + 2 * tilePadding) * totalCols, tileWH + 2 * tilePadding));

            //para cada coluna
            for (int col = 0; col < totalCols; col++) {
                //seta a altura e largura do tile
                tiles[row][col].setLayoutParams(new LayoutParams(tileWH + 2 * tilePadding, tileWH + 2 * tilePadding));
                //adiciona o espacamento do tile
                tiles[row][col].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
                //adiciona o tile ao table row
                tableRow.addView(tiles[row][col]);
            }
            //adiciona a linha para o layoute do campo minado
            mineField.addView(tableRow, new TableLayout.LayoutParams((tileWH + 2 * tilePadding) * totalCols, tileWH + 2 * tilePadding));
        }
    }

    public void startTimer() {
        if (secondsPassed == 0) {
            timer.removeCallbacks(updateTimer);
            timer.postDelayed(updateTimer, 1000);
            secondsPassed = 1;
            timerText.setText("00" + secondsPassed);
        }
    }

    public void stopTimer() {
        timer.removeCallbacks(updateTimer);
    }

    public void setMinesText() {
        if (minesCount < 10) {
            minesText.setText("00" + minesCount);
        } else if (minesCount < 100) {
            minesText.setText("0" + minesCount);
        } else {
            minesText.setText(minesCount);
        }
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            String curTime = Integer.toString(secondsPassed);
            //atualize o texto da view
            if (secondsPassed < 10) {
                timerText.setText("00" + curTime);
            } else if (secondsPassed < 100) {
                timerText.setText("0" + curTime);
            } else {
                timerText.setText(curTime);
            }
            timer.postAtTime(this, currentMilliseconds);
            //roda de novo em 1 segundo
            timer.postDelayed(updateTimer, 1000);
        }
    };

    private void loseGame() {
        imageButton.setBackgroundResource(R.drawable.sad);

        MediaPlayer media = MediaPlayer.create(getApplicationContext(), R.raw.bomb);
        media.start();

        endGame();
    }

    private void winGame() {
        imageButton.setBackgroundResource(R.drawable.smile);

        endGame();

        Intent intent = new Intent(Game.this, Score.class);
        intent.putExtra(Score.KEY_SCORE, "" + secondsPassed);
        startActivity(intent);

    }

    public boolean checkWonGame() {
        if (totalCoveredTiles == totalMines || correctFlags == totalMines)
            return true;
        else
            return false;
    }

    // RIPPLE EFFECT
    private void uncoverTiles(int row, int col) {
        //se o tile for uma mina ou uma bandeira retorne
        if (tiles[row][col].isMine() || tiles[row][col].isFlag())
            return;

        tiles[row][col].openTile();
        if (!tiles[row][col].isMine())
            totalCoveredTiles--;

        if (tiles[row][col].getNoSurroundingMines() > 0)
            return;


        int startRow = row - 1;
        int startCol = col - 1;
        int checkRows = 3;
        int checkCols = 3;
        if (startRow < 0) {
            startRow = 0;
            checkRows = 2;
        } else if (startRow + 3 > totalRows)
            checkRows = 2;

        if (startCol < 0) {
            startCol = 0;
            checkCols = 2;
        } else if (startCol + 3 > totalCols)
            checkCols = 2;

        for (int i = startRow; i < startRow + checkRows; i++) {
            for (int j = startCol; j < startCol + checkCols; j++) {
                if (tiles[i][j].isCovered())
                    uncoverTiles(i, j);
            }
        }
    }

    private void endGame() {
        stopTimer();

        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                //se o tile estiver coberto
                if (tiles[i][j].isCovered()) {
                    //se tiver nenhuma bandeira ou mina
                    if (!tiles[i][j].isFlag() && !tiles[i][j].isMine()) {
                        tiles[i][j].openTile();
                    }
                    //se tiver uma mina mas nao tiver bandeira
                    else if (tiles[i][j].isMine() && !tiles[i][j].isFlag()) {
                        tiles[i][j].openTile();
                    }
                }
            }
        }


    }

    private void restartGame() {
        stopTimer();
        //remove os table rows do layout do campo minado
        mineField.removeAllViews();
        //reseta as variaveis
        timerStarted = false;
        minesSet = false;
        secondsPassed = 0;
        timerText.setText("0");

        createGameBoard(diff);
        setupMineField(totalRows, totalCols);
        showGameBoard();

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://campominado.com.jean.campominado/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://campominado.com.jean.campominado/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
