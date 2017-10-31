package eu.epitech.jcoinche;

public class Team {

    private int mScoreTot;
    private int mCurrentScore;
    private boolean mIsBidding;
    private boolean mHasBeAndRe;
    private Player.PlayerId mPlayerOne;
    private Player.PlayerId mPlayerTwo;

    public Team(Player.PlayerId play1, Player.PlayerId play2) {
        mCurrentScore = 0;
        mScoreTot = 0;
        mIsBidding = false;
        mPlayerOne = play1;
        mPlayerTwo = play2;
        mHasBeAndRe = false;
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

    public void reset() {
        mCurrentScore = 0;
        mIsBidding = false;
        mHasBeAndRe = false;
    }

    public void resetGame() {
        reset();
        mScoreTot = 0;
    }

    void updateScore(int valueTrick) {
        mCurrentScore += valueTrick;
    }

    void updateScoreTotal(int score) {
        mScoreTot += score;
    }

    public int getTotalScore() { return mScoreTot; }
}
