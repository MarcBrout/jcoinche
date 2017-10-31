package eu.epitech.jcoinche;

public class Team {

    private int mScoreTot;
    private int mCurrentScore;
    boolean mIsBidding;
    Player.PlayerId mPlayerOne;
    Player.PlayerId mPlayerTwo;

    public Team(Player.PlayerId play1, Player.PlayerId play2) {
        mCurrentScore = 0;
        mScoreTot = 0;
        mIsBidding = false;
        mPlayerOne = play1;
        mPlayerTwo = play2;
    }

    Player.PlayerId getPlayerOne() {
        return mPlayerOne;
    }

    Player.PlayerId getPlayerTwo() {
        return mPlayerTwo;
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

    void reset() {
        mCurrentScore = 0;
        mIsBidding = false;
    }

    void resetGame() {
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
