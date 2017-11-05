package jar;

import eu.epitech.jcoinche.*;
import eu.epitech.jcoinche.CoincheProtocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.pmw.tinylog.Logger;

import java.util.Scanner;

public class CoincheClientHandler extends SimpleChannelInboundHandler<Message> {
    private String mUserName;
    private Player mPlay;
    private Table table = new Table();
    private String mLastBid;

    private void sendBid(ChannelHandlerContext channelHandlerContext) throws Exception {
        String elBid = mPlay.bid(mLastBid);
        if (elBid.equals("Coinche") || elBid.equals("Surcoinche")) {
            if (channelHandlerContext.channel().isOpen())
                channelHandlerContext.writeAndFlush(Translator.buildBid(mUserName, mPlay.getmPlayerId(), elBid, 0, ""));
            return;
        }
        String color = elBid.substring(elBid.length() - 1);
        if (elBid.substring(0, elBid.length()).equals("Pass") ||
            elBid.substring(0, elBid.length()).equals("Capot") ||
            elBid.substring(0, elBid.length()).equals("General")) {
            if (channelHandlerContext.channel().isOpen())
                channelHandlerContext.writeAndFlush(Translator.buildBid(mUserName, mPlay.getmPlayerId(), elBid.substring(0, elBid.length()), 0, color));
            return;
        }
        if (channelHandlerContext.channel().isOpen())
            channelHandlerContext.writeAndFlush(Translator.buildBid(mUserName, mPlay.getmPlayerId(),elBid.substring(0, elBid.length() - 1), Integer.valueOf(elBid.substring(0, elBid.length() - 1)), color));
    }

    private void sendCard(ChannelHandlerContext channelHandlerContext) throws Exception {
        mPlay.showHand();
        table.showTable();
        BaseCard card = mPlay.play(table);
        table.putCardTable(card);
        if (channelHandlerContext.channel().isOpen())
            channelHandlerContext.writeAndFlush(Translator.buildCardRequested(mPlay, card));
    }

    private void sendName(ChannelHandlerContext channelHandlerContext) throws Exception {
        String name;
        do {
            System.out.print("Please, enter your name : ");
            Scanner scan = new Scanner(System.in);
            name = scan.nextLine();
        } while (name.equals(""));
        mUserName = name;
        if (channelHandlerContext.channel().isOpen())
            channelHandlerContext.writeAndFlush(Translator.buildName(mUserName));
    }

    private void treatRequest(ChannelHandlerContext channelHandlerContext, Message.Request req) throws Exception {
        switch (req) {
            case BID:
                sendBid(channelHandlerContext);
                break;
            case CARD:
                sendCard(channelHandlerContext);
                break;
            case NAME:
                sendName(channelHandlerContext);
                break;
            case QUIT:
                channelHandlerContext.writeAndFlush(Translator.buildQuitConfirmation());
                channelHandlerContext.close();
                break;
        }
    }

    private void enteringInState(CoincheProtocol.Info.State state)
    {
        switch (state) {
            case ENDING:
                System.out.println("End of the game");
                break;
            case GIVING:
                System.out.println("Begin the distribution");
                table.endGame();
                mPlay.reset();
                break;
            case BIDDING:
                mPlay.showHand();
                System.out.println("It's time to BID");
                break;
            case PLAYING:
                System.out.println("Run just started");
                break;
            case WAITING:
                System.out.println("Waiting other player to connect");
                break;
        }
    }

    private String determinePlayer(int id) {
        switch (id) {
            case 0:
                return "Player1";
            case 1:
                return "Player2";
            case 2:
                return "Player3";
            case 3:
                return "Player4";
        }
        return "";
    }

    private Player.PlayerId determinePlayerId(int id) {
        switch (id) {
            case 0:
                return Player.PlayerId.PLAYER_ONE;
            case 1:
                return Player.PlayerId.PLAYER_TWO;
            case 2:
                return Player.PlayerId.PLAYER_THREE;
            case 3:
                return Player.PlayerId.PLAYER_FOUR;
        }
        return Player.PlayerId.NB_PLAYER;
    }

    private void determineColor(CoincheProtocol.Bid bid)
    {
        switch (bid.getTrumpColor()) {
            case CLUBS:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid " + String.valueOf(bid.getAmount()) + " of Clubs.");
                mLastBid = String.valueOf(bid.getAmount()); break;
            case HEART:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid " + String.valueOf(bid.getAmount()) + " of Heart.");
                mLastBid = String.valueOf(bid.getAmount()); break;
            case SPADES:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid " + String.valueOf(bid.getAmount()) + " of Spades.");
                mLastBid = String.valueOf(bid.getAmount()); break;
            case DIAMONDS:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid " + String.valueOf(bid.getAmount()) + " of Diamonds.");
                mLastBid = String.valueOf(bid.getAmount()); break;
        }
    }

    private void enteringInBid(CoincheProtocol.Bid bid)
    {
        mPlay.setCoinchePossible(true);
        switch (bid.getCall()) {
            case PASS:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has passed.");
                mLastBid = "Pass"; break;
            case CAPOT:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid Capot");
                mLastBid = "Capot"; break;
            case NORMAL:
                determineColor(bid); break;
            case COINCHE:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid Coinche");
                mPlay.setSurCoinchePossible(true);
                mLastBid = "Coinche"; break;
            case GENERAL:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid General");
                mLastBid = "General"; break;
            case SURCOINCHE:
                System.out.println(bid.getName() + " (" + determinePlayer(bid.getId()) + ") " + "has bid Surcoinche");
                mLastBid = "Surcoinche"; break;
        }
    }

    private String detectTheColor(CoincheProtocol.Card.Color color) {
        switch (color) {
            case HEART:
                return "Heart";
            case SPADES:
                return "Spades";
            case DIAMONDS:
                return "Diamonds";
            case CLUBS:
                return "Clubs";
        }
        return "";
    }

    private void enteringInCard(CoincheProtocol.Card card) {
        table.putCardTable(Translator.translateCard(card));
        switch (card.getRank()) {
            case AS:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played an As of " + detectTheColor(card.getColor())); break;
            case TEN:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played a Ten of " + detectTheColor(card.getColor())); break;
            case JACK:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played a Jack of " + detectTheColor(card.getColor())); break;
            case KING:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played a King of " + detectTheColor(card.getColor())); break;
            case NINE:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played a Nine of " + detectTheColor(card.getColor())); break;
            case QUEEN:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played a Queen of " + detectTheColor(card.getColor())); break;
            case SEVEN:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played a Seven of " + detectTheColor(card.getColor())); break;
            case EIGHT:
                System.out.println(card.getName() + " (" + determinePlayer(card.getId()) + ") " + "has played an Eight of " + detectTheColor(card.getColor())); break;
        }
    }

    private void giveCard(CoincheProtocol.Hand hand) throws Exception {
        for (CoincheProtocol.Card card : hand.getCardList()) {
            mPlay.takeCard(Translator.translateCard(card));
        }
    }

    private void treatInfo(ChannelHandlerContext channelHandlerContext, CoincheProtocol.Info info) throws Exception {
        switch (info.getType()) {
            case BID:
                enteringInBid(info.getBid()); break;
            case CARD:
                enteringInCard(info.getCard()); break;
            case STATE:
                enteringInState(info.getState()); break;
            case HAND:
                giveCard(info.getHand()); break;
            case TRICK:
                table.clearTable();
                System.out.println(info.getTrick() + " has won the trick !"); break;
            case LEAVER:
                System.out.println(info.getLeaver() + " has left the game..."); break;
            case ROUND:
                System.out.println("End of the round. " + info.getEnd().getNamePlay1() + " and " + info.getEnd().getNamePlay2() +
                        " won with : " + info.getEnd().getCurrentScore() + " points !");
                mPlay.reset();
                mLastBid = "Pass";
                break;
            case END:
                System.out.println("End of the game. " + info.getEnd().getNamePlay1() + " and " + info.getEnd().getNamePlay2() +
                        " won with : " + info.getEnd().getTotalScore() + " points !");
                mPlay.reset();
                mLastBid = "Pass";
                break;
            case NEWPLAYER:
                System.out.println("A new player; " + info.getNewPlayer() + " has just joined the game !"); break;
            case AUCTIONWINNER:
                mPlay.transformToTrump(info.getBid().getTrumpColor());
                System.out.println(info.getBid().getName() + " has won the auction !"); break;
        }
    }

    private void treatValidation(ChannelHandlerContext channelHandlerContext, Message msg) throws Exception {
        if (!msg.getOk())
            channelHandlerContext.close();
        else
            mPlay = new Player(mUserName, determinePlayerId(msg.getId()), channelHandlerContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Message msg) throws Exception {
        switch (msg.getType()) {
            case REQUEST:
                treatRequest(channelHandlerContext, msg.getRequest()); break;
            case INFO:
                treatInfo(channelHandlerContext, msg.getInfo()); break;
            case VALIDATION:
                treatValidation(channelHandlerContext, msg); break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        Logger.error(cause.getMessage());
        Logger.debug(cause);
        context.close();
    }
}

