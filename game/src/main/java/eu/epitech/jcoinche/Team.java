package eu.epitech.jcoinche;

public class Team {

    private int mScoreTot;
    private int mCurrentScore;
    private int mCurClearScore;
    private boolean mIsBidding;
    private boolean mHasBeAndRe;
    private boolean mHasWin;
    private Player.PlayerId mPlayerOne;
    private Player.PlayerId mPlayerTwo;

    public Team(Player.PlayerId play1, Player.PlayerId play2) {
        mCurrentScore = 0;
        mCurClearScore = 0;
        mScoreTot = 0;
        mIsBidding = false;
        mPlayerOne = play1;
        mPlayerTwo = play2;
        mHasBeAndRe = false;
        mHasWin = false;
    }

    Player.PlayerId getPlayerOne() {
        return mPlayerOne;
    }

    Player.PlayerId getPlayerTwo() {
        return mPlayerTwo;
    }

    public void teamHasBeAndRe() { mHasBeAndRe = true;}

    public boolean isHasBeAndRe() {
        return mHasBeAndRe;
    }

    public void teamHasBid() {
        mIsBidding = true;
    }

    public int getCurrentScore() {
        return mCurrentScore;
    }

    public boolean isBidding() {
        return mIsBidding;
    }

    public void setCurClearScore(int score) {
        mCurClearScore = score;
    }

    public int getCurClearScore() { return mCurClearScore; }

    public boolean isWinning() {
        return mHasWin;
    }

    public void won() {
        mHasWin = true;
    }

    public void reset() {
        mCurrentScore = 0;
        mCurClearScore = 0;
        mIsBidding = false;
        mHasBeAndRe = false;
        mHasWin = false;
    }

    public void resetGame() {
        reset();
        mScoreTot = 0;
    }

    void updateScore(int valueTrick) {
        mCurrentScore += valueTrick;
    }

    void updateScoreTotal(int score) {
        setCurClearScore(score);
        mScoreTot += score;
    }

    public int getTotalScore() { return mScoreTot; }
}
