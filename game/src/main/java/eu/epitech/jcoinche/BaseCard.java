package eu.epitech.jcoinche;

import org.pmw.tinylog.Logger;

public class BaseCard {
    public enum Rank {
        SEVEN,
        EIGHT,
        NINE,
        JACK,
        QUEEN,
        KING,
        TEN,
        AS,
        BAD_RANK
    }

    public enum Color {
        HEART,
        SPADE,
        CLUB,
        DIAMOND,
        BAD_COLOR
    }

    static private int[] values = {0, 0, 0, 2, 3, 4, 10, 11};
    static private int[] trumpValues = {0, 0, 3, 4, 10, 11, 14, 20};
    private static String[] rankStr = {"7", "8", "9", "J", "Q", "K", "10", "A"};
    private static char[] colorStr = {'H', 'S', 'C', 'D'};

    private Color color = Color.BAD_COLOR;
    private Rank rank = Rank.BAD_RANK;
    private boolean isPlayed = false;
    private boolean trump = false;
    private Player.PlayerId playerId = Player.PlayerId.NB_PLAYER;

    public Player.PlayerId getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Player.PlayerId playerId) {
        this.playerId = playerId;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public Color getColor() {
        return color;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isLike(BaseCard c) {
        return (this.color == c.color &&
                this.playerId == c.playerId &&
                this.rank == c.rank);
    }

    protected boolean isBadCard() {
        return (color.compareTo(Color.BAD_COLOR) >= 0 || rank.compareTo(Rank.BAD_RANK) >= 0);
    }

    BaseCard(Color color, Rank rank) {
        this.color = color;
        this.rank = rank;
    }

    void swap(BaseCard other) {
        Rank rankStock = this.rank;
        Color colorStock = this.color;
        boolean playedStock = this.isPlayed;

        this.rank = other.rank;
        this.color = other.color;
        this.isPlayed = other.isPlayed;

        other.rank = rankStock;
        other.color = colorStock;
        other.isPlayed = playedStock;
    }

    public boolean isTrump() {
        return trump;
    }

    public void setTrump(boolean trump) {
        this.trump = trump;
    }

    public int getValue() {
        if (trump)
            return (trumpValues[rank.ordinal()]);
        return (values[rank.ordinal()]);
    }

    @Override
    public String toString() {
        return rankStr[rank.ordinal()] + colorStr[color.ordinal()];
    }
}
