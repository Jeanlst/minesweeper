package campominado.com.jean.campominado;

/**
 * Created by Jean on 21/02/2016.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class Tile extends Button {
    private boolean isMine;
    private boolean isFlag;
    private boolean isQuestionMark;
    private boolean isCovered;
    private int noSurroundingMines;

    public Tile(Context context) {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDefaults() {
        isMine = false;
        isFlag = false;
        isQuestionMark = false;
        isCovered = true;
        noSurroundingMines = 0;

        this.setBackgroundResource(R.drawable.tile);
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }

    public void setQuestionMark(boolean questionMark) {
        isQuestionMark = questionMark;
    }

    public void setUncovered() {
        isCovered = false;
    }

    public void updateSurroundingMineCount() {
        noSurroundingMines++;
    }

    public void openTile() {
        if(!isCovered)
            return;

        setUncovered();
        if(this.isMine())
            triggerMine();
        else
            showNumber();
    }

    public void plantMine() {
        isMine = true;
    }

    public void triggerMine() {
        this.setBackgroundResource(R.drawable.mine);
    }

    public void showNumber() {
        if (noSurroundingMines > 0) {
            String img = "mines" + noSurroundingMines;
            Log.i("debug", this + " - " + noSurroundingMines);
            int drawableId = getResources().getIdentifier(img, "drawable", "campominado.com.jean.campominado");
            this.setBackgroundResource(drawableId);
        } else {
            this.setBackgroundResource(R.drawable.untile);
        }
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public int getNoSurroundingMines() {
        return noSurroundingMines;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public boolean isQuestionMark() { return isQuestionMark; }

    public void setFlagIcon() {
        this.setBackgroundResource(R.drawable.flag);
    }

    public void setQuestionMarkIcon() {
        this.setBackgroundResource(R.drawable.question);
    }

    public void setCoveredIcon() {
        this.setBackgroundResource(R.drawable.tile);
    }
}