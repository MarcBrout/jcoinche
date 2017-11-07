package eu.epitech.jcoinche;

import eu.epitech.jcoinche.CoincheProtocol.Bid;

import javax.print.DocFlavor;

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

    private int checkBeAndRe(Team team) {
        if (team.isHasBeAndRe())
            return 20;
        else
            return 0;
    }

    private void addingScore(Team team1, Team team2)
    {
        if (team1.getCurrentScore() >= 81)
            team1.updateScore(20);
        if (team1.getCurrentScore() >= bestBid.getAmount()) {
            team1.won();
            if (team1.getCurrentScore() >= 162)
                team1.updateScoreTotal((250 + checkBeAndRe(team1) +  bestBid.getAmount()) * mMult);
            else {
                team1.updateScoreTotal((Count.getRealScore(team1.getCurrentScore() - checkBeAndRe(team1)) + checkBeAndRe(team1) + bestBid.getAmount()) * mMult);
                team2.updateScoreTotal(Count.getRealScore(team2.getCurrentScore()) + checkBeAndRe(team2));
            }
        }
        else {
            team2.won();
            team2.updateScoreTotal((160 + checkBeAndRe(team2) + bestBid.getAmount()) * mMult);
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
    }

    public void resetRun() {
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
        mMult = 1;
    }
}
