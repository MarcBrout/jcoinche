package eu.epitech.jcoinche;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.pmw.tinylog.Logger;

import java.util.*;

public class Player {

    public enum PlayerId {
        PLAYER_ONE,
        PLAYER_TWO,
        PLAYER_THREE,
        PLAYER_FOUR,
        NB_PLAYER
    }

    private boolean coinchePossible = false;
    private boolean surCoinchePossible = false;

    public void setCoinchePossible(boolean coinchePossible) {
        if (!surCoinchePossible)
            this.coinchePossible = coinchePossible;
    }

    public void setSurCoinchePossible(boolean surCoinchePossible) {
        this.surCoinchePossible = surCoinchePossible;
    }

    private ArrayList<BaseCard> mHand = new ArrayList<BaseCard>();
    private PlayerId mTeamMate;
    private PlayerId mPlayerId = PlayerId.NB_PLAYER;
    private String mName;
    private ChannelHandlerContext ctx;

    public void reset() {
        coinchePossible = false;
        surCoinchePossible = false;
        mHand.clear();
    }
    public void close() {
        if (ctx != null) {
            ctx.channel().closeFuture();
            ctx = null;
        }
    }

    public void listen(Message msg) {
        if (ctx != null)
            ctx.writeAndFlush(msg);
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    private String bid = null;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    private int mIdxCard = 0;

    public List<BaseCard> getmHand() {
        return Collections.unmodifiableList(mHand);
    }

    public Player(String name, PlayerId playerId, ChannelHandlerContext ctx) {
        this.mName = name;
        this.mPlayerId = playerId;
        this.ctx = ctx;
    }

    public void showHand() {
        System.out.println("This is your hand : ");
        System.out.println("count : " + mHand.size());
        for (BaseCard card : mHand) {
            System.out.print(card.toString() + " ");
        }
        System.out.print("\n");
    }

    public void takeCard(BaseCard card) {
        mHand.add(card);
    }

    private boolean isCardPossessed(String card) {
        for (BaseCard inHand:
             mHand) {
            if (card.equals(inHand.toString()))
                return true;
        }
        return false;
    }

    private void determineCardInHand(String card) throws Exception {
        int idx = 0;
        for (BaseCard inHand:
             mHand) {
            Logger.info("HAND TO STRING {}", inHand);
            if (inHand.toString().equals(card)) {
                mIdxCard = idx;
                return ;
            }
            idx++;
        }
        throw  new Exception("Error: Hand of cards seems have changed during execution.");
    }

    private boolean handHasTrump() {
        for (BaseCard card:
             mHand) {
            if (card.isTrump())
                return true;
        }
        return false;
    }

    private boolean trumpHasMoreValueInHand(BaseCard cmp)
    {
        for (BaseCard card:
             mHand) {
            if (card.isTrump() && card.getValue() > cmp.getValue())
                return true;
        }
        return false;
    }

    private boolean handHasTrumpStronger(int value)
    {
        for (BaseCard card:
             mHand) {
            if (card.isTrump() && card.getValue() > value)
                return true;
        }
        return false;
    }

    private boolean hasSameColorInHand(BaseCard.Color color)
    {
        for (BaseCard card:
             mHand) {
            if (card.getColor() == color)
                return true;
        }
        return false;
    }

    private boolean testTrump(Table table) throws Exception {
        if (!mHand.get(mIdxCard).isTrump()) {
            if (handHasTrump())
                return false;
            else
                return true;
        }
        else {
            if (table.ismTrumpPlayed())
            {
                if (mHand.get(mIdxCard).getValue() < table.getMaxPlayedTrump().getValue())
                {
                    return !handHasTrumpStronger(table.getMaxPlayedTrump().getValue());
                }
                else
                    return true;
            }
            else
                return true;
        }
    }

    private boolean isCardPlayable(String card, Table table) throws Exception {
        determineCardInHand(card);
        if (table.getNbCardOnTable() == 0)
            return true;
        if (table.isTurnTrump()) {
            return testTrump(table);
        }
        else {
            BaseCard.Color firstColor = table.getFirstColor();
            if (mHand.get(mIdxCard).getColor() != firstColor) {
                if (hasSameColorInHand(firstColor))
                    return false;
                return testTrump(table);
            }
            else
                return true;
        }
    }

    public BaseCard play(Table table) throws Exception {
        boolean pass = false;

        System.out.print("Please, select a card to play: ");
        String text;
        do {
            if (pass)
                System.out.print("\nError: You can't play this card.\nPlease select a valid card: ");
            pass = true;
            Scanner scan = new Scanner(System.in);
            text = scan.nextLine();
        } while (!isCardPossessed(text) || !isCardPlayable(text, table));
        return mHand.remove(mIdxCard);
    }

    private void showHelpBid() {
        System.out.println("Wrong input, please select one of the following instruction :" +
            "\nPass / 80 / 90 / 100 / 110 / 120 / 130 / 140 / 150 / 160 / Capot / General / Coinche / Surcoinche\n\n");
    }

    private void showHelpColor() {
        System.out.println("Wrong input, please select one of the following instruction :" +
            "\nH / D / S / C");
    }

    public String bid(String lastBid) {
        boolean pass = false;
        int idxPow;
        int myPow;
        ArrayList<String> bidValue = new ArrayList<String>() {{
            add("Pass");
            add("80");
            add("90");
            add("100");
            add("110");
            add("120");
            add("130");
            add("140");
            add("150");
            add("160");
            add("Capot");
            add("General");
            add("Coinche");
            add("Surcoinche");
        }};
        ArrayList<String> bidColor = new ArrayList<String>() {{
            add("H");
            add("D");
            add("S");
            add("C");
        }};
        String text;
        String bidText;

        idxPow = bidValue.indexOf(lastBid);
        do {
            if (pass)
                showHelpBid();
            System.out.print("Please, select a bid value: ");
            pass = true;
            Scanner scan = new Scanner(System.in);
            text = scan.nextLine();
            myPow = bidValue.indexOf(text);
        } while (myPow == -1 || (myPow != 0 && myPow <= idxPow) || (text.equals("Coinche") && !coinchePossible) ||
                (text.equals("Surcoinche") && !surCoinchePossible));
        if (text.equals("Coinche") && coinchePossible)
        {
            coinchePossible = false;
        }
        if (myPow == 0 || myPow > 11)
            return text;
        pass = false;
        bidText = text;
        do {
            if (pass)
                showHelpColor();
            System.out.print("Please, select a color: ");
            pass = true;
            Scanner scan = new Scanner(System.in);
            text = scan.nextLine();
        } while (!bidColor.contains(text));
        bidText += text;
        return bidText;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public PlayerId  getmPlayerId() {
        return mPlayerId;
    }

    public void setmPlayerId(PlayerId mPlayerId) {
        this.mPlayerId = mPlayerId;
    }

    public PlayerId getmTeamMate() {
        return mTeamMate;
    }

    public void setmTeamMate(PlayerId mTeamMate) {
        this.mTeamMate = mTeamMate;
    }

    public void putCard(BaseCard card, Table table) {
        for (int i = 0; i < mHand.size(); ++i)
        {
            if (card.isLike(mHand.get(i)))
            {
                table.takeCard(mHand.remove(i));
                break;
            }
        }
    }

    public void giveHandToDealer(Dealer dealer) {
        while (!mHand.isEmpty())
        {
            dealer.takeCard(mHand.remove(mHand.size() - 1));
        }
    }

    public void removeCard(BaseCard card) {
        BaseCard toRemove = null;

        for (int i = 0; i < mHand.size(); ++i)
        {
            toRemove = mHand.get(i);

            if (card.getColor() == toRemove.getColor() &&
                    card.getRank() == toRemove.getRank())
                break;
        }
        mHand.remove(toRemove);
    }
    public void transformToTrump(CoincheProtocol.Card.Color color) {
        for (BaseCard card : mHand) {
            if (card.getColor().ordinal() == color.ordinal())
                card.setTrump(true);
        }
    }

    @Override
    public String toString() {
        return "Player : " + mName + " id : " + mPlayerId.toString();
    }
}
