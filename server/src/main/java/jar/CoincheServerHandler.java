package jar;

import eu.epitech.jcoinche.*;
import eu.epitech.jcoinche.CoincheProtocol.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.pmw.tinylog.Logger;

public class CoincheServerHandler extends SimpleChannelInboundHandler<Message> {

    private static int id = 0;
    private static int bidCounter = 0;
    private static Dealer dealer = new Dealer();
    private static Table table = new Table();
    private static Team team1 = new Team(Player.PlayerId.PLAYER_ONE, Player.PlayerId.PLAYER_THREE);
    private static Team team2 = new Team(Player.PlayerId.PLAYER_TWO, Player.PlayerId.PLAYER_FOUR);
    private static Score score = new Score(team1, team2);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        switch (msg.getType())
        {
            case ANSWER: resolveAnswer(msg.getAnswer(), ctx);
                break;
            case LEAVE: resolvePlayerLeaving(ctx);
                break;
            default:
                break;
        }
    }

    private void resolvePlayerLeaving(ChannelHandlerContext ctx) {
        Player p = dealer.getPlayer(ctx);

        Logger.info("Player '{}' left, resetting server state", p.getmName());
        dealer.sendMessageToEveryone(Translator.buildLeaverMessage(p), p.getmPlayerId());
        dealer.sendMessageToEveryone(Translator.buildQuitRequest());
        dealer.closeAllPlayerExcept(p);
        resetGame(true);
        dealer.goToState(Info.State.WAITING);
    }

    // Called once when a connection succeeded
    @Override
    public void channelActive(final ChannelHandlerContext context) {
        Logger.info("New client connected");
        Message req = Message.newBuilder()
                .setType(Message.Type.REQUEST)
                .setRequest(Message.Request.NAME)
                .build();

        context.writeAndFlush(req);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        Player p = dealer.getPlayer(context);

        Logger.info("Player '{}' had connexion trouble", p.getmName());
        dealer.sendMessageToEveryone(Translator.buildLeaverMessage(p), p.getmPlayerId());
        dealer.sendMessageToEveryone(Translator.buildQuitRequest());

        resetGame(true);

        p.getCtx().channel().closeFuture();
        Logger.info(cause.getMessage());
    }

    private void resolveAnswer(Answer ans, ChannelHandlerContext ctx) {
        switch (ans.getType())
        {
            case NAME: tryAddPlayer(ans, ctx);
                break;
            case BID: sendBidToPlayers(ans.getBid(), ctx);
                break;
            case CARD: checkCardPlayed(ans.getCard(), ctx);
                break;
            case QUIT: ctx.channel().closeFuture();
        }
    }

    private void sendBidToPlayers(Bid bid, ChannelHandlerContext ctx) {
        Player p = dealer.getPlayer(ctx);

        if (p != null) {
            dealer.sendMessageToEveryone(Translator.buildBidMessage(bid, p), p.getmPlayerId());
            switch (bid.getCall()) {
                case PASS:
                    processPassCall(bid);
                    break;
                case NORMAL:
                    processNormalCall(bid);
                    break;
                case CAPOT:
                    processNormalCall(bid);
                    break;
                case GENERAL:
                    processNormalCall(bid);
                    break;
                case COINCHE:
                    processNormalCall(bid);
                    break;
                case SURCOINCHE:
                    processSurCoincheCall(bid);
                    break;
            }
        }
    }

    private void launchGame(Player p, Bid bid) {
        Logger.info("Launching game, first player '{}'.", p.getmName());
        if (score.isInTeamOne(p.getmPlayerId()))
            team1.teamHasBid();
        else
            team2.teamHasBid();

        Player belotter = dealer.findBelotteEtRe(Translator.translateColor(bid.getTrumpColor()));

        if (belotter != null) {
            if (score.isInTeamOne(belotter.getmPlayerId())) {
                team1.teamHasBeAndRe();
            } else {
                team2.teamHasBeAndRe();
            }
        }

        dealer.sendMessageToEveryone(Translator.buildAuctionWinner(p, score));
        dealer.goToState(Info.State.PLAYING);
        dealer.sendMessageTo(Translator.buildCardRequest(), dealer.getPlayerById(Player.PlayerId.values()[id % 4]));
        bidCounter = 0;
    }

    private void processSurCoincheCall(Bid bid) {
        Logger.info("Player '{}' is announcing {} at {} color",
                bid.getName(), bid.getAmount(), bid.getTrumpColor().name());
        launchGame(dealer.getPlayer(bid.getName()), bid);
        bidCounter = 0;
    }

    private void processNormalCall(Bid bid) {
        Logger.info("Player '{}' is announcing '{}'", bid.getName(), bid.getAmount());
        if (bid.getCall() != Bid.Call.COINCHE)
            score.setBestBidder(bid);
        dealer.sendMessageTo(Translator.buildBidRequest(),
                dealer.getPlayerById(getNextPlayer(bid)));
        bidCounter = 0;
    }

    private void resetGame(boolean fullReset) {
        if (fullReset) {
            Logger.debug("Doing full game reset");
            score.reset();
            dealer.hardReset();
            id = 0;
            bidCounter = 0;
        } else {
            Logger.debug("Doing partial game reset");
            dealer.reset();
        }
    }

    private void relaunchNewGame() {
        Logger.info("Relaunching the game");
        resetGame(false);
        dealer.goToState(Info.State.GIVING);
        dealer.cutDeck();
        dealer.distribute();
        dealer.goToState(Info.State.BIDDING);
        bidCounter = 0;
    }

    private Player.PlayerId getNextPlayer(Bid bid) {
        return Player.PlayerId.values()[(bid.getId() + 1) % 4];
    }

    private Player.PlayerId getFirstPlayer(Bid bid) {
        return Player.PlayerId.values()[(Player.PlayerId.PLAYER_ONE.ordinal() + id) % 4];
    }

    private void processPassCall(Bid bid)
    {
        Logger.info("Player '{}' is passing.", bid.getName());
        if (bidCounter > 0) {
            Bid bidWinner = score.getBestBidder();

            if (bidWinner != null) {
                Player p = dealer.getPlayer(bidWinner.getName());
                ++bidCounter;

                if (bidCounter == 3) {
                    Logger.info("Player '{}' has won the bid !", p.getmName());
                    launchGame(p, bid);
                } else {
                    dealer.sendMessageTo(Translator.buildBidRequest(), dealer.getPlayerById(getNextPlayer(bid)));
                }
            } else if (bidCounter == 3) {
                Logger.info("4 pass in a row");
                relaunchNewGame();
                dealer.sendMessageTo(Translator.buildBidRequest(), dealer.getPlayerById(getFirstPlayer(bid)));
            } else {
                dealer.sendMessageTo(Translator.buildBidRequest(), dealer.getPlayerById(getNextPlayer(bid)));
                ++bidCounter;
            }
        } else {
            dealer.sendMessageTo(Translator.buildBidRequest(), dealer.getPlayerById(getNextPlayer(bid)));
            ++bidCounter;
        }
    }

    private boolean endRound() {
        return table.getTrick() == 8;
    }

    private boolean endGame() {
        return team1.getTotalScore() > 3000 || team2.getCurrentScore() > 3000;
    }

    private Team returnWinningTeam() {
        return (team1.isWinning()) ? team1 : team2;
    }

    private void checkCardPlayed(Card card, ChannelHandlerContext ctx) {
        try {
            dealer.watchPutCardTable(Translator.translateCard(card), ctx, table);
            Player played = dealer.getPlayer(card.getName());
            dealer.sendMessageToEveryone(Translator.buildCardInfo(card), played.getmPlayerId());
            if (table.getmTurn() == 4)
            {
                Logger.info("Card received : {}", card);
                Logger.info("Strongest card : {}", table.getStrongestCard());
                Player strongest = dealer.getPlayerById(table.getStrongestCard().getPlayerId());
                Logger.info("Winner : {}", strongest.getmName());
                score.updateScore(strongest.getmPlayerId(), table.getTrickScore());
                dealer.sendMessageToEveryone(Translator.buildTrickWinnerMessage(strongest));
                while (table.hasCard())
                    dealer.takeCard(table.giveCard());
                table.clearTable();
                if (endRound()) {
                    score.endRun();
                    dealer.sendMessageToEveryone(Translator.buildEndRound(returnWinningTeam(), dealer));
                    score.resetRun();
                    ++id;
                    if (endGame()) {
                        dealer.sendMessageToEveryone(Translator.buildEndGame(returnWinningTeam(), dealer));
                        dealer.sendMessageToEveryone(Translator.buildQuitRequest());
                        dealer.closeAllPlayerExcept();
                        dealer.goToState(Info.State.WAITING);
                        resetGame(true);
                    } else {
                        table.endGame();
                        dealer.goToState(Info.State.GIVING);
                        dealer.cutDeck();
                        dealer.distribute();
                        dealer.goToState(Info.State.BIDDING);
                        dealer.sendMessageTo(Translator.buildBidRequest(),
                                dealer.getPlayerById(Player.PlayerId.values()[id % 4]));
                    }
                } else {
                    dealer.sendMessageTo(Translator.buildCardRequest(), strongest);
                }
            } else {
                dealer.sendMessageTo(Translator.buildCardRequest(),
                        dealer.getPlayerById(Player.PlayerId.values()[(played.getmPlayerId().ordinal() + 1) % 4]));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void tryAddPlayer(Answer ans, ChannelHandlerContext ctx)
    {
        Player p = new Player(ans.getName(), Player.PlayerId.values()[id % 4], ctx);

        try {
            dealer.addPlayer(p);
        } catch (Exception e) {
            dealer.sendMessageTo(Translator.buildValidation(false,p.getmPlayerId().ordinal(), p.getmName()), p);
            return;
        }
        Logger.info("New player {} joined the table. waiting for {} more player(s)",
                p.getmName(), 4 - dealer.getPlayerCount());
        dealer.sendMessageTo(Translator.buildValidation(true, p.getmPlayerId().ordinal(), p.getmName()), p);
        dealer.sendMessageToEveryone(Translator.buildNewPlayer(p), p.getmPlayerId());
        if (dealer.isTableFull()) {
            dealer.goToState(Info.State.GIVING);
            dealer.shuffleDeck();
            dealer.distribute();
            dealer.goToState(Info.State.BIDDING);
            dealer.sendMessageTo(Translator.buildBidRequest(), dealer.getPlayerById(Player.PlayerId.PLAYER_ONE));
        } else {
            dealer.goToState(Info.State.WAITING);
        }
        ++id;
    }
}