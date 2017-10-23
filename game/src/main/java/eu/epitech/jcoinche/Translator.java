package eu.epitech.jcoinche;

import eu.epitech.jcoinche.CoincheProtocol.*;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;

public class Translator {
    static public Card.Color translateColor(BaseCard.Color color) {
        return Card.Color.values()[color.ordinal()];
    }

    static public BaseCard.Color translateColor(Card.Color color) {
        return BaseCard.Color.values()[color.ordinal()];
    }

    static public Card.Rank translateRank(BaseCard.Rank rank) {
        return Card.Rank.values()[rank.ordinal()];
    }

    static public BaseCard.Rank translateRank(Card.Rank rank) {
        return BaseCard.Rank.values()[rank.ordinal()];
    }

    static public Card translateCard(BaseCard card, Player p) {
        return Card.newBuilder()
                .setColor(translateColor(card.getColor()))
                .setRank(translateRank(card.getRank()))
                .setTrump(card.isTrump())
                .setId(p.getmPlayerId().ordinal())
                .setName(p.getmName())
                .build();
    }

    static public BaseCard translateCard(Card card) {
        BaseCard c = new BaseCard(translateColor(card.getColor()), translateRank(card.getRank()));

        c.setPlayerId(Player.PlayerId.values()[card.getId()]);
        if (card.getTrump()) {
            c.setTrump(true);
        }
        return c;
    }

    static public Message buildHand(ArrayList<Card> hand, String details) {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.HAND)
                        .setHand(Hand.newBuilder()
                                .addAllCard(hand)
                                .build())
                        .build())
                .setDescription(details)
                .build();
    }

    static public Message buildName(String name)
    {
        return Message.newBuilder()
                .setType(Message.Type.ANSWER)
                .setAnswer(Answer.newBuilder()
                        .setType(Message.Request.NAME)
                        .setName(name).build())
                .build();
    }

    static public Message buildValidation(boolean ok, int id, String details) {
        return Message.newBuilder()
                .setType(Message.Type.VALIDATION)
                .setOk(ok)
                .setDescription(details)
                .setId(id)
                .build();
    }

    static public Message buildCardRequest()
    {
        Logger.info("Sending Card request");
        return Message.newBuilder()
                .setType(Message.Type.REQUEST)
                .setRequest(Message.Request.CARD)
                .build();
    }

    static public Message buildBid(String name, Player.PlayerId id, String bid, int amount, String color) {
        ArrayList<String> call = new ArrayList<String>() {{
            add("Pass");
            add("Normal");
            add("Capot");
            add("General");
            add("Coinche");
            add("Surcoinche");
        }};
        ArrayList<String> col = new ArrayList<String>() {{
            add("H");
            add("S");
            add("C");
            add("D");
        }};
        int callIdx = call.indexOf(bid);
        int colorIdx = col.indexOf(color);

        if (callIdx == 2)
            amount = 250;
        else if (callIdx == 3)
            amount = 500;
        if (callIdx == -1)
            callIdx = 1;
        if (colorIdx == -1)
            colorIdx = 4;
        Logger.info("Color idx = {}", colorIdx);
        return Message.newBuilder()
                .setType(Message.Type.ANSWER)
                .setAnswer(Answer.newBuilder()
                        .setType(Message.Request.BID)
                        .setBid(Bid.newBuilder()
                                .setCall(Bid.Call.values()[callIdx])
                                .setName(name)
                                .setAmount(amount)
                                .setTrumpColor(Card.Color.values()[colorIdx])
                                .setId(id.ordinal())
                                .build())
                        .build())
                .build();
    }

    static public Message buildCardRequested(Player p, BaseCard card) {
        Logger.info("player build the card {}", p);
        return Message.newBuilder()
                .setType(Message.Type.ANSWER)
                .setAnswer(Answer.newBuilder()
                        .setType(Message.Request.CARD)
                        .setCard(translateCard(card, p))
                        .build())
                .setDescription(p.getmName())
                .setId(p.getmPlayerId().ordinal())
                .build();
    }

    static public Message buildCardInfo(Card card) {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.CARD)
                        .setCard(card)
                        .build())
                .build();
    }

    static public Message buildQuitConfirmation() {
        return Message.newBuilder()
                .setType(Message.Type.ANSWER)
                .setAnswer(Answer.newBuilder()
                        .setType(Message.Request.QUIT)
                        .build())
                .build();
    }

    static public Message buildQuitRequest() {
        return Message.newBuilder()
                .setType(Message.Type.REQUEST)
                .setRequest(Message.Request.QUIT)
                .build();
    }

    static public Message buildBidRequest() {
        Logger.info("Sending Bid request");
        return Message.newBuilder()
                .setType(Message.Type.REQUEST)
                .setRequest(Message.Request.BID)
                .build();
    }

    static public Message buildStateChangeInfo(Info.State state)
    {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.STATE)
                        .setState(state)
                        .build())
                .build();
    }

    static public Message buildTrickWinnerMessage(Player p)
    {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.TRICK)
                        .setTrick(p.getmName())
                        .build())
                .build();
    }

    static public Message buildLeaverMessage(Player p)
    {
        return Message.newBuilder()
                .setType(Message.Type.LEAVE)
                .setDescription(p.getmName())
                .build();
    }

    static public Message buildBidMessage(Bid bid, Player p)
    {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.BID)
                        .setBid(bid)
                        .build())
                .build();
    }

    static public Message buildNewPlayer(Player p)
    {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.NEWPLAYER)
                        .setNewPlayer(p.getmName())
                        .build())
                .build();
    }

    static public Message buildAuctionWinner(Player p, Score score) {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.AUCTIONWINNER)
                        .setAuctionWinner(p.getmName())
                        .setBid(score.getBestBidder())
                        .build())
                .setId(p.getmPlayerId().ordinal())
                .build();
    }

    static public Message buildEndGame(Team winners, Dealer dealer) {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.END)
                        .setEnd(End.newBuilder()
                                .setNamePlay1(dealer.getPlayerName(winners.getPlayerOne()))
                                .setNamePlay2(dealer.getPlayerName(winners.getPlayerTwo()))
                                .setTotalScore(winners.getTotalScore())
                                .build())
                        .build())
                .build();
    }

    static public Message buildEndRound(Team winners, Dealer dealer)
    {
        return Message.newBuilder()
                .setType(Message.Type.INFO)
                .setInfo(Info.newBuilder()
                        .setType(Info.Type.ROUND)
                        .setEnd(End.newBuilder()
                                .setNamePlay1(dealer.getPlayerName(winners.getPlayerOne()))
                                .setNamePlay2(dealer.getPlayerName(winners.getPlayerTwo()))
                                .setCurrentScore(winners.getCurrentScore())
                                .build())
                        .build())
                .build();
    }
}
