package eu.epitech.jcoinche;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Table {
    private ArrayList<BaseCard> mCardPlayed = new ArrayList<BaseCard>(4);
    private int mTurn = 0;
    private int mTrick = 0;
    private boolean mTrumpPlayed = false;
    private boolean mIsTurnTrump = false;
    private BaseCard.Color mFirstColor = BaseCard.Color.BAD_COLOR;
    private BaseCard strongestCard = null;

    public BaseCard getStrongestCard() {
        return strongestCard;
    }

    private int trickScore = 0;

    public boolean isTurnTrump() {
        return mIsTurnTrump;
    }

    public BaseCard.Color getFirstColor() {
        return mFirstColor;
    }

    public boolean ismTrumpPlayed() {
        return mTrumpPlayed;
    }

    public int getmTurn() {
        return mTurn;
    }

    public int getTrick() {
        return mTrick;
    }

    public int getNbCardOnTable() {
        return mCardPlayed.size();
    }

    public int getTrickScore() {
        return trickScore;
    }

    public BaseCard getMaxPlayedTrump() {
        int i = 0;
        int res = 0;
        int value = 0;

        for (BaseCard card:
                mCardPlayed) {
            if (card.isTrump() && card.getValue() > value) {
                value = card.getValue();
                res = i;
            }
            i++;
        }
        return (mCardPlayed.get(res));
    }

    private boolean isFirstTurn() {
        return (mTurn == 0);
    }

    private boolean canPlayerPutThisCard(Player player, BaseCard card) {
        if (player.getmPlayerId() != card.getPlayerId())
            return false;

        for (BaseCard c : player.getmHand())
        {
            if (c.isLike(card) && !c.isPlayed())
            {
                c.setPlayed(true);
                return true;
            }
        }
        return false;
    }

    private int determinateScore(BaseCard card) {
        int score = 0;
        if (mTrick == 7 && mTurn == 3)
            score += 10; // 10 de DER
        score += card.getValue();
        return score;
    }

    public boolean putCardTable(BaseCard card) {
        if (isFirstTurn()) {
            mTrick++;
            Logger.info("Trick turn : {}", mTrick);
            if (card.isTrump())
                mIsTurnTrump = true;
            mFirstColor = card.getColor();
        }
        if (!mTrumpPlayed && card.isTrump())
            mTrumpPlayed = true;
        mCardPlayed.add(card);
        trickScore += determinateScore(card);
        if (mCardPlayed.size() > 1)
            strongestCard = whoWonTheTrick();
        else
            strongestCard = card;
        mTurn++;
        return true;
    }

    public void takeCard(BaseCard card)
    {
        mCardPlayed.add(card);
    }

    public void clearTable() {
        mTurn = 0;
        mFirstColor = BaseCard.Color.BAD_COLOR;
        mIsTurnTrump = false;
        mTrumpPlayed = false;
        mCardPlayed.clear();
        trickScore = 0;
        strongestCard = null;
        Logger.debug("Clearing Table");
    }

    public void endGame() {
        mTrick = 0;
        clearTable();
    }

    public void showTable() {
        System.out.println("\nTable STATE :\n");
        for (BaseCard card : mCardPlayed) {
            System.out.print(card.toString() + " | ");
        }
        System.out.print("\n\n");
    }

    public BaseCard giveCard() throws Exception {
        if (mCardPlayed.isEmpty())
            throw new Exception("No more Card on the table");
        return mCardPlayed.remove(0);
    }

    private BaseCard high()
    {
        BaseCard strong = null;
        BaseCard.TrumpRank rank = BaseCard.TrumpRank.SEVEN;

        for (BaseCard card : mCardPlayed)
        {
            if (card.isTrump() && card.getTrumpRank().compareTo(rank) >= 0)
            {
                rank = card.getTrumpRank();
                strong = card;
            }
        }
        return strong;
    }

    private BaseCard high(BaseCard.Color color)
    {
        BaseCard strong = null;
        BaseCard.Rank rank = BaseCard.Rank.SEVEN;

        for (BaseCard card : mCardPlayed)
        {
            if (card.getColor() == color && card.getRank().compareTo(rank) >= 0)
            {
                rank = card.getRank();
                strong = card;
            }
        }
        return strong;
    }

     BaseCard whoWonTheTrick()
    {
        if (mIsTurnTrump || mTrumpPlayed)
        {
            return high();
        }
        return high(mFirstColor);
    }

    public boolean hasCard() {
        return !mCardPlayed.isEmpty();
    }
}
