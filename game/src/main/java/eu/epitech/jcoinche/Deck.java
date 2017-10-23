package eu.epitech.jcoinche;

import java.util.ArrayList;
import java.util.Random;

public class Deck {

    private Random randGenerator = new Random(System.currentTimeMillis());
    private ArrayList<BaseCard> shuffledDeck = new ArrayList<BaseCard>();
    private ArrayList<BaseCard> deck = new ArrayList<BaseCard>();

    private int getRandomInt(int size) {
        return randGenerator.nextInt(size);
    }

    public void shuffle() {
        while (!deck.isEmpty()) {
            shuffledDeck.add(deck.remove(getRandomInt(deck.size())));
        }
    }

    public void cut() {
        int cut = getRandomInt(shuffledDeck.size() - 1) + 1;

        for (int i = 0; i < cut; i++)
        {
            shuffledDeck.add(shuffledDeck.remove(i));
        }
        shuffledDeck.forEach((x) -> x.setTrump(false));
    }

    public BaseCard pop() throws Exception
    {
        if (shuffledDeck.size() == 0)
            throw new Exception("No more card in the Deck");
        return shuffledDeck.remove(shuffledDeck.size() - 1);
    }

    public void add(BaseCard card)
    {
        shuffledDeck.add(card);
    }

    public void reset() {
        if (!shuffledDeck.isEmpty())
            shuffledDeck.clear();
        if (!deck.isEmpty())
            deck.clear();
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.SEVEN));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.EIGHT));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.NINE));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.TEN));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.JACK));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.QUEEN));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.KING));
        deck.add(new BaseCard(BaseCard.Color.HEART, BaseCard.Rank.AS));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.SEVEN));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.EIGHT));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.NINE));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.TEN));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.JACK));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.QUEEN));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.KING));
        deck.add(new BaseCard(BaseCard.Color.DIAMOND, BaseCard.Rank.AS));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.SEVEN));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.EIGHT));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.NINE));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.TEN));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.JACK));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.QUEEN));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.KING));
        deck.add(new BaseCard(BaseCard.Color.SPADE, BaseCard.Rank.AS));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.SEVEN));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.EIGHT));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.NINE));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.TEN));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.JACK));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.QUEEN));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.KING));
        deck.add(new BaseCard(BaseCard.Color.CLUB, BaseCard.Rank.AS));
    }

    public Deck() {
        this.reset();
    }
}
