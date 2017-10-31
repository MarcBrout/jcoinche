package eu.epitech.jcoinche;

import eu.epitech.jcoinche.CoincheProtocol.Bid;

public class Score {
    private Team mTeam1;
    private Team mTeam2;
    private Bid bestBid = null;
    private int mMult = 1;

    public Bid getBestBidder() {
        return bestBid;
    }

    public void setBestBidder(Bid bestBidder) {
        this.bestBid = bestBidder;
        if (bestBid.getCall() == Bid.Call.COINCHE)
            mMult = 2;
        else if (bestBid.getCall() == Bid.Call.SURCOINCHE)
            mMult = 4;
        else
            mMult = 1;
    }

    public Score(Team first, Team second) {
        this.mTeam1 = first;
        this.mTeam2 = second;
    }

    public boolean isInTeamOne(Player.PlayerId id) {
        return (mTeam1.getPlayerOne() == id || mTeam1.getPlayerTwo() == id);
    }

    public void updateScore(Player.PlayerId playerId, int score) {
        if (isInTeamOne(playerId)) {
            mTeam1.updateScore(score);
        }
        else {
            mTeam2.updateScore(score);
        }
    }

    private void addingScore(Team team1, Team team2)
    {
        if (team1.getCurrentScore() >= bestBid.getAmount()) {
            if (team1.getCurrentScore() == 162)
                team1.updateScoreTotal((250 + bestBid.getAmount()) * mMult);
            else {
                team1.updateScoreTotal(Count.getRealScore((team1.getCurrentScore() + bestBid.getAmount()) * mMult));
                team2.updateScoreTotal(Count.getRealScore(team2.getCurrentScore()));
            }
        }
        else {
            team2.updateScoreTotal((160 + bestBid.getAmount()) * mMult);
        }
    }

    public void endRun()
    {
        if (mTeam1.isBidding()) {
            addingScore(mTeam1, mTeam2);
        }
        else {
            addingScore(mTeam2, mTeam1);
        }
        mTeam1.reset();
        mTeam2.reset();
        bestBid = null;
        mMult = 1;
    }

    public void reset()
    {
        mTeam1.resetGame();
        mTeam2.resetGame();
        bestBid = null;
    }
}
