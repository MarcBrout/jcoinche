package eu.epitech.jcoinche;

import eu.epitech.jcoinche.CoincheProtocol.*;
import io.netty.channel.ChannelHandlerContext;
import org.pmw.tinylog.Logger;

import javax.swing.plaf.metal.MetalBorders;
import java.util.ArrayList;

public class Dealer {
    private ArrayList<Player> players = new ArrayList<Player>(4);
    private Deck deck = new Deck();
    private int gameCount = 0;

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isTableFull() {
        return players.size() == 4;
    }

    public void shuffleDeck() {
        deck.shuffle();
    }

    public void cutDeck() {
        deck.cut();
    }

    public void takeCard(BaseCard card) {
        deck.add(card);
    }

    public Player getPlayerById(Player.PlayerId id) {
        for (Player p : players)
        {
            if  (p.getmPlayerId() == id)
                return p;
        }
        return null;
    }

    public BaseCard giveCard() throws Exception {
        return deck.pop();
    }

    public BaseCard giveCardToPlayer(Player player) throws Exception {
        BaseCard card = deck.pop();
        card.setPlayerId(player.getmPlayerId());
        return card;
    }

    public void addPlayer(Player player) throws Exception  {
        if (players.size() == 4)
            throw new Exception("No more room for any new player");

        players.add(player);
    }

    public void endGame(Table table) throws Exception {
        players.clear();
        deck.reset();
        table.endGame();
    }

    private Player.PlayerId whoWonTheTrick(Table table) {
        return table.whoWonTheTrick().getPlayerId();
    }

    public Player.PlayerId takeCardsFromTable(Table table) throws Exception {
        Player.PlayerId id;

        id = whoWonTheTrick(table);
        takeCard(table.giveCard());
        takeCard(table.giveCard());
        takeCard(table.giveCard());
        takeCard(table.giveCard());
        return id;
    }

    public void sendMessageToEveryone(Message msg) {
        sendMessageToEveryone(msg, Player.PlayerId.NB_PLAYER);
    }

    public void sendMessageToEveryone(Message msg, Player.PlayerId not) {
        for (Player p : players)
        {
            if (p.getmPlayerId() != not)
                p.listen(msg);
        }
    }

    public void sendMessageTo(Message msg, Player player)
    {
        player.getCtx().writeAndFlush(msg);
    }

    private ArrayList<Card> buildHandsToGive(int count, Player p) throws Exception {
        ArrayList<Card> hand = new ArrayList<Card>();

        while (count > 0)
        {
            BaseCard card = giveCard();
            p.takeCard(card);
            hand.add(Translator.translateCard(card, p));
            --count;
        }
        return hand;
    }

    private void distributeNCards(int count) throws Exception {
        for (Player p : players)
        {
            sendMessageTo(Translator.buildHand(buildHandsToGive(count, p), p.getmName()), p);
        }
    }

    public void distribute() {
        try {
            distributeNCards(2);
            distributeNCards(3);
            distributeNCards(3);
        } catch (Exception e) {
            System.out.println("Error occured during distribution !");
        }
    }

    public String getPlayerName(Player.PlayerId id)
    {
        for (Player p : players)
        {
            if (p.getmPlayerId() == id)
                return p.getmName();
        }
        return null;
    }

    public Player getPlayer(ChannelHandlerContext ctx)
    {
        for (Player p : players)
        {
            if (p.getCtx() == ctx)
                return p;
        }
        return null;
    }

    public Player getPlayer(String name)
    {
        for (Player p : players)
        {
            if (p.getmName().equals(name))
                return p;
        }
        return null;
    }

    public void reset() {
        for (Player p : players)
        {
            p.giveHandToDealer(this);
        }
    }

    public void closeAllPlayerExcept() {
        closeAllPlayerExcept(null);
    }

    public void closeAllPlayerExcept(Player p) {
        for (Player player : players) {
            if (p == null || player.getmPlayerId() != p.getmPlayerId())
                player.close();
        }
    }

    public void hardReset() {
        try {
            deck.reset();
            players.clear();
        } catch (Exception ignored) {
        }
    }

    public boolean watchPutCardTable(BaseCard card, ChannelHandlerContext ctx, Table table) {
        Player p = getPlayer(ctx);
        p.removeCard(card);
        return table.putCardTable(card);
    }

    public void goToState(Info.State state) {
        Logger.info("Changing state : {}", state.toString());
        goToState(state, null);
    }

    public void goToState(Info.State state, Player p) {
        if (p != null)
            sendMessageToEveryone(Translator.buildStateChangeInfo(state), p.getmPlayerId());
        else
            sendMessageToEveryone(Translator.buildStateChangeInfo(state));
    }
}
