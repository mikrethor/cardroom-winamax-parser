package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.mikrethor.cardroom.enums.Card;
import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.EAction;
import fr.mikrethor.cardroom.enums.GameType;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.Player;

public class WinamaxParsingTest {

	@Test
	public void testGetCards() {
		final String nextLine = "9h Ks As";
		final WinamaxParsing siteParsing = new WinamaxParsing(null);
		final String[] tab = siteParsing.getCards(nextLine);
		assertStringTab(new String[] { "9h", "Ks", "As" }, tab);

	}

	@Test
	public void testToCards() {
		final String nextLine = "9h Ks As";
		final WinamaxParsing siteParsing = new WinamaxParsing(null);
		final String[] tab = siteParsing.getCards(nextLine);
		final Card[] tabCard = siteParsing.toECards(tab);
		assertCardTab(new Card[] { Card.C_9H, Card.C_KS, Card.C_AS }, tabCard);

	}

	@Test
	public void testStringToCards() {
		final String nextLine = "9h";
		final WinamaxParsing siteParsing = new WinamaxParsing(null);
		final Card eCard = siteParsing.stringToECards(nextLine);
		Assert.assertEquals(Card.C_9H, eCard);

	}

	@Test
	public void testGetPlayerBlind() {
		// TODO Auto-generated method stub

	}

	private void assertStringTab(String[] expected, String[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int index = 0; index < expected.length; index++) {
			Assert.assertEquals(expected[index], actual[index]);
		}
	}

	private void assertCardTab(Card[] expected, Card[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int index = 0; index < expected.length; index++) {
			Assert.assertEquals(expected[index], actual[index]);
		}
	}

	@Test
	public void parsingSNGFile() {
		final String fileName = "./target/test-classes/20170107_NOCTAMBULE(180688705)_real_holdem_no-limit.txt";

		final File file = new File(fileName);
		final WinamaxParsing siteParsing = new WinamaxParsing(file);
		Map<String, Hand> hands = null;

		Assert.assertEquals(true, file.exists());
		Assert.assertEquals(true, file.isFile());
		// Parsing
		final long debut = System.currentTimeMillis();

		hands = siteParsing.parse();

		Assert.assertNotNull(hands);
		final long fin = System.currentTimeMillis();

		Assert.assertEquals(97, hands.values().size());
		// Check fisrt hand
		String handId = "103356159434";
		Hand main1 = hands.get(handId);
		Assert.assertEquals(9, main1.getNbPlayersOnOneTable());

		final Player dragoonnhead = main1.getPlayers().get(1);
		final Player sirius369 = main1.getPlayers().get(2);
		final Player acrisdu11 = main1.getPlayers().get(3);
		final Player polo21544 = main1.getPlayers().get(4);
		final Player backsidair = main1.getPlayers().get(5);
		final Player nivekash = main1.getPlayers().get(6);
		final Player mikrethor = main1.getPlayers().get(7);
		final Player bakoly = main1.getPlayers().get(8);
		final Player misterwill8 = main1.getPlayers().get(9);
		//
		// Check players seats
		Assert.assertEquals(Integer.valueOf(1), dragoonnhead.getSeat());
		Assert.assertEquals(Integer.valueOf(2), sirius369.getSeat());
		Assert.assertEquals(Integer.valueOf(3), acrisdu11.getSeat());
		Assert.assertEquals(Integer.valueOf(4), polo21544.getSeat());
		Assert.assertEquals(Integer.valueOf(5), backsidair.getSeat());
		Assert.assertEquals(Integer.valueOf(6), nivekash.getSeat());
		Assert.assertEquals(Integer.valueOf(7), mikrethor.getSeat());
		Assert.assertEquals(Integer.valueOf(8), bakoly.getSeat());
		Assert.assertEquals(Integer.valueOf(9), misterwill8.getSeat());
		// Check players names
		Assert.assertEquals("dragoonnhead", dragoonnhead.getName());
		Assert.assertEquals("Sirius369", sirius369.getName());
		Assert.assertEquals("acrisdu11", acrisdu11.getName());
		Assert.assertEquals("polo21544", polo21544.getName());
		Assert.assertEquals("backsidair", backsidair.getName());
		Assert.assertEquals("nivekash", nivekash.getName());
		Assert.assertEquals("mikrethor", mikrethor.getName());
		Assert.assertEquals("bakoly", bakoly.getName());
		Assert.assertEquals("misterwill8", misterwill8.getName());
		// // Check last player stack
		Assert.assertEquals(new Double(1500), new Double(dragoonnhead.getStack()));
		Assert.assertEquals(new Double(1500), new Double(sirius369.getStack()));
		Assert.assertEquals(new Double(1500), new Double(acrisdu11.getStack()));
		Assert.assertEquals(new Double(1500), new Double(polo21544.getStack()));
		Assert.assertEquals(new Double(1500), new Double(backsidair.getStack()));
		Assert.assertEquals(new Double(1500), new Double(nivekash.getStack()));
		Assert.assertEquals(new Double(1500), new Double(mikrethor.getStack()));
		Assert.assertEquals(new Double(1500), new Double(bakoly.getStack()));
		Assert.assertEquals(new Double(1500), new Double(misterwill8.getStack()));

		// final Hand hand = game.getHands().get(1);
		Assert.assertEquals(handId, main1.getLabel());

		Assert.assertEquals(Double.valueOf(20), Double.valueOf(main1.getBigBlind()));
		Assert.assertEquals(Double.valueOf(10), Double.valueOf(main1.getSmallBlind()));

		Assert.assertEquals(acrisdu11, main1.getBigBlindPlayer());
		Assert.assertEquals(sirius369, main1.getSmallBlindPlayer());
		Assert.assertEquals(dragoonnhead, main1.getDealerPlayer());
		Assert.assertEquals(mikrethor, main1.getPlayer());

		Assert.assertEquals("1", main1.getIdTable());
		Assert.assertEquals(Double.valueOf(0), Double.valueOf(main1.getRake()));

		Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(main1.getLevel()));

		// [8c 8d Qd]
		final Card[] flop = main1.getFlop();
		Assert.assertEquals(new WinamaxParsing(null).stringToECards("8c"), flop[0]);
		Assert.assertEquals(new WinamaxParsing(null).stringToECards("8d"), flop[1]);
		Assert.assertEquals(new WinamaxParsing(null).stringToECards("Qd"), flop[2]);

		// Pas de turn dans le fichier
		final Card turn = main1.getTurn();
		Assert.assertEquals(null, turn);

		// Pas de river dans le fichier
		final Card river = main1.getRiver();
		Assert.assertEquals(null, river);

		Assert.assertEquals(Double.valueOf(50), Double.valueOf(main1.getTotalPot()));

		Assert.assertEquals(Integer.valueOf(9), Integer.valueOf(main1.getPreflopActions().size()));

		final List<Action> listPreFlopActions = main1.getPreflopActions();

		Assert.assertEquals(polo21544, listPreFlopActions.get(0).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(0).getAction());
		Assert.assertEquals(backsidair, listPreFlopActions.get(1).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(1).getAction());
		Assert.assertEquals(nivekash, listPreFlopActions.get(2).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(2).getAction());
		Assert.assertEquals(mikrethor, listPreFlopActions.get(3).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(3).getAction());
		Assert.assertEquals(bakoly, listPreFlopActions.get(4).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(4).getAction());
		Assert.assertEquals(misterwill8, listPreFlopActions.get(5).getPlayer());
		Assert.assertEquals(EAction.CALLS, listPreFlopActions.get(5).getAction());
		Assert.assertEquals(dragoonnhead, listPreFlopActions.get(6).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(6).getAction());
		Assert.assertEquals(sirius369, listPreFlopActions.get(7).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(7).getAction());
		Assert.assertEquals(acrisdu11, listPreFlopActions.get(8).getPlayer());
		Assert.assertEquals(EAction.CHECKS, listPreFlopActions.get(8).getAction());

		Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(main1.getFlopActions().size()));

		final List<Action> listFlopActions = main1.getFlopActions();

		Assert.assertEquals(acrisdu11, listFlopActions.get(0).getPlayer());
		Assert.assertEquals(EAction.CHECKS, listFlopActions.get(0).getAction());
		Assert.assertEquals(misterwill8, listFlopActions.get(1).getPlayer());
		Assert.assertEquals(EAction.BETS, listFlopActions.get(1).getAction());
		Assert.assertEquals(acrisdu11, listFlopActions.get(2).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listFlopActions.get(2).getAction());
		Assert.assertEquals(misterwill8, listFlopActions.get(3).getPlayer());
		Assert.assertEquals(EAction.COLLECTED, listFlopActions.get(3).getAction());

		Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(main1.getTurnActions().size()));
		Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(main1.getRiverActions().size()));
		Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(main1.getShowdownActions().size()));

		handId = "103357830872";
		main1 = hands.get(handId);

		Card[] cards = main1.getMapPlayerCards().get("mikrethor");
		Assert.assertEquals(Card.C_2S, cards[0]);
		Assert.assertEquals(Card.C_7C, cards[1]);

		cards = main1.getMapPlayerCards().get("dragoonnhead");
		Assert.assertEquals(Card.C_6D, cards[0]);
		Assert.assertEquals(Card.C_7S, cards[1]);

		cards = main1.getMapPlayerCards().get("misterwill8");
		Assert.assertEquals(Card.C_JD, cards[0]);
		Assert.assertEquals(Card.C_4D, cards[1]);

	}

	@Test

	public void testParseTableId() {
		String nextLine = "Table: 'NOCTAMBULE(180688705)#004' 6-max (real money) Seat #1 is the button";
		final WinamaxParsing siteParsing = new WinamaxParsing(null);

		Assert.assertEquals("004", siteParsing.parseTableId(nextLine));
		nextLine = "Table: 'Freeroll(180688949)#0239' 6-max (real money) Seat #1 is the button";
		Assert.assertEquals("0239", siteParsing.parseTableId(nextLine));
	}

	@Test

	public void testParseLevel() {
		// Cas heads-up with rebuy
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final WinamaxParsing siteParsing = new WinamaxParsing(null);

		Assert.assertEquals(1, siteParsing.parseLevel(nextLine));
		nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 10 - HandId: #776052078731591685-96-1483840063 - Holdem no limit (100/500/1000) - 2017/01/08 01:47:43 UTC";
		Assert.assertEquals(10, siteParsing.parseLevel(nextLine));

	}

	@Test

	public void testParse() {
		// TODO Auto-generated method stub

	}

	@Test

	public void testReadCards() {
		// TODO Auto-generated method stub

	}

	@Test

	public void testRead() {
		// TODO Auto-generated method stub

	}

	@Test

	public void testParseable() {
		// TODO Auto-generated method stub

	}

	@Test

	public void testNextLine() {
		// TODO Auto-generated method stub

	}

	@Test

	public void testGetTournamentId() {
		// TODO Auto-generated method stub

	}

	@Test

	public void testSetDevise() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals(Double.valueOf(3.5), siteParsing.parseBuyIn(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(0.25), siteParsing.parseBuyIn(nextLine));
	}

	@Test
	public void testParsing() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadActionsByPhase() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadNewHand() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadTable() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadSeats() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadPreflop() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadFlop() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadTurn() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadRiver() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadShowdown() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadSummary() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testReadAntesBlinds() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testParseBuyIn() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals(Double.valueOf(3.5), siteParsing.parseBuyIn(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(0.25), siteParsing.parseBuyIn(nextLine));
	}

	@Test
	public void testParseFee() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals(Double.valueOf(0.39), siteParsing.parseFee(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(0.03), siteParsing.parseFee(nextLine));
	}

	@Test
	public void testParseHandIdSite() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals("119469642172", siteParsing.parseHandIdSite(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals("118890204785", siteParsing.parseHandIdSite(nextLine));
	}

	@Test
	public void testParseSmallBlind() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals(Double.valueOf(10), siteParsing.parseSmallBlind(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(10), siteParsing.parseSmallBlind(nextLine));
	}

	@Test
	public void testParseBigBlind() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals(Double.valueOf(20), siteParsing.parseBigBlind(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(20), siteParsing.parseBigBlind(nextLine));
	}

	@Test
	public void testParseNumberOfPlayerByTable() {
		String nextLine = "Table '945696315 1' 9-max Seat #1 is the button";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals("9", siteParsing.parseNumberOfPlayerByTable(nextLine));
		nextLine = "Table '939130332 1' 9-max Seat #1 is the button";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals("9", siteParsing.parseNumberOfPlayerByTable(nextLine));
	}

	@Test
	public void testParseGameIdSite() {
		String nextLine = "PokerStars Hand #119469642172: Tournament #945696315, $3.11+$0.39 USD Hold'em No Limit - Level I (10/20) - 2014/07/30 21:26:57 ET";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals("945696315", siteParsing.parseGameIdSite(nextLine));
		nextLine = "PokerStars Hand #118890204785: Tournament #939130332, €0.22+€0.03 EUR Hold'em No Limit - Level I (10/20) - 2014/07/17 23:31:35 CET [2014/07/17 17:31:35 ET]";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals("939130332", siteParsing.parseGameIdSite(nextLine));
	}

	@Test
	public void testParseButtonSeat() {
		String nextLine = "Table '945696315 1' 9-max Seat #1 is the button";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		Assert.assertEquals(Integer.valueOf(1), siteParsing.parseButtonSeat(nextLine));
		nextLine = "Table '939130332 1' 9-max Seat #9 is the button";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Integer.valueOf(9), siteParsing.parseButtonSeat(nextLine));

	}

	@Test
	public void testParsePlayerSeat() {
		String nextLine = "Seat 2: Mikkel B 72 (1500 in chips)";
		final ICardroomParser siteParsing = new WinamaxParsing(null);

		Assert.assertEquals("Mikkel B 72", siteParsing.parsePlayerSeat(nextLine).getName());
		nextLine = "Seat 8: -Cr@zyChips- (1500 in chips) ";
		Assert.assertEquals("-Cr@zyChips-", siteParsing.parsePlayerSeat(nextLine).getName());

	}

	// @Test
	// public void testParsePlayerSummary() {
	// String nextLine = "Seat 1: dragoonnhead (big blind) mucked [6d 7s]";
	//
	// final WinamaxParsing siteParsing = new WinamaxParsing(null);
	//
	// Player player = siteParsing.parsePlayerSummary(nextLine);
	// Assert.assertEquals("dragoonnhead", player.getName());
	// Assert.assertEquals(Integer.valueOf(1), player.getSeat());
	// Assert.assertEquals(Card.C_6D, player.getCards()[0]);
	// Assert.assertEquals(Card.C_7S, player.getCards()[1]);
	//
	// nextLine = "Seat 3: acrisdu11 folded before Flop (didn't bet)";
	// player = siteParsing.parsePlayerSummary(nextLine);
	// Assert.assertEquals("acrisdu11", player.getName());
	// Assert.assertEquals(Integer.valueOf(3), player.getSeat());
	// Assert.assertNull(player.getCards());
	//
	// nextLine = "Seat 5: backsidair folded before Flop (didn't bet)";
	// player = siteParsing.parsePlayerSummary(nextLine);
	// Assert.assertEquals("backsidair", player.getName());
	// Assert.assertEquals(Integer.valueOf(5), player.getSeat());
	// Assert.assertNull(player.getCards());
	//
	// nextLine = "Seat 6: nivekash folded before Flop (didn't bet)";
	// player = siteParsing.parsePlayerSummary(nextLine);
	// Assert.assertEquals("nivekash", player.getName());
	// Assert.assertEquals(Integer.valueOf(6), player.getSeat());
	// Assert.assertNull(player.getCards());
	//
	// nextLine = "Seat 7: mikrethor (button) folded before Flop (didn't bet)";
	// player = siteParsing.parsePlayerSummary(nextLine);
	// Assert.assertEquals("mikrethor", player.getName());
	// Assert.assertEquals(Integer.valueOf(7), player.getSeat());
	// Assert.assertNull(player.getCards());
	//
	// nextLine = "Seat 9: misterwill8 (small blind) showed [Jd 4d] and won
	// (200) with high card Ace";
	// player = siteParsing.parsePlayerSummary(nextLine);
	// Assert.assertEquals("misterwill8", player.getName());
	// Assert.assertEquals(Integer.valueOf(9), player.getSeat());
	// Assert.assertEquals(Card.C_JD, player.getCards()[0]);
	// Assert.assertEquals(Card.C_4D, player.getCards()[1]);
	//
	// }

	@Test
	public void testParseTotalPot() {
		String nextLine = "Total pot 130 | Rake 0 ";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(130), siteParsing.parseTotalPot(nextLine));
		nextLine = "Total pot €0.12 | Rake €0 ";
		Assert.assertEquals(Double.valueOf(0.12), siteParsing.parseTotalPot(nextLine));
		nextLine = "Total pot 18.80 | Rake 0 ";
		Assert.assertEquals(Double.valueOf(18.80), siteParsing.parseTotalPot(nextLine));
		siteParsing.setCurrency(Currency.USD);
		nextLine = "Total pot $0.12 | Rake $0 ";
		Assert.assertEquals(Double.valueOf(0.12), siteParsing.parseTotalPot(nextLine));
	}

	@Test
	public void testParseRake() {
		String nextLine = "Total pot 130 | Rake 15 ";
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(15), siteParsing.parseRake(nextLine));
		nextLine = "Total pot €0.12 | Rake €0.02 ";
		Assert.assertEquals(Double.valueOf(0.02), siteParsing.parseRake(nextLine));
		nextLine = "Total pot 1880 | Rake 0 ";
		Assert.assertEquals(Double.valueOf(0), siteParsing.parseRake(nextLine));
		siteParsing.setCurrency(Currency.USD);
		nextLine = "Total pot $0.12 | Rake $0.11 ";
		Assert.assertEquals(Double.valueOf(0.11), siteParsing.parseRake(nextLine));
	}

	@Test
	public void testParsePlayerAccount() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testGetGameTypeFromFilename() {
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);
		String fileName = "HH20140717 T939130332 Hold'em No Limit 0,22 € + 0,03 €.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "HH20140723 T939346140 Omaha Pot Limit Freeroll.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "HH20140726 T939346000 Hold'em No Limit Freeroll.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "HH20140728 T928868040 Hold'em No Limit Ticket.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "HH20140728 T944506158 Hold'em No Limit 3,11 $ + 0,39 $.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "HH20141023 Endeavour II - 0,02 $-0,05 $ - USD Hold'em No Limit.txt";
		Assert.assertEquals(GameType.CASH, siteParsing.getGameTypeFromFilename(fileName));
	}

	@Test
	public void testIsUselesLine() {
		final ICardroomParser siteParsing = new WinamaxParsing(null);
		siteParsing.setCurrency(Currency.USD);

		Assert.assertTrue(siteParsing.isUselesLine("will be allowed to play after the button"));
		Assert.assertTrue(siteParsing.isUselesLine("posts small & big blinds"));
		Assert.assertTrue(siteParsing.isUselesLine("posts the ante"));
		Assert.assertTrue(siteParsing.isUselesLine("sits out "));
		Assert.assertTrue(siteParsing.isUselesLine("leaves the table"));
		Assert.assertTrue(siteParsing.isUselesLine("is sitting out"));
		Assert.assertTrue(siteParsing.isUselesLine("is disconnected "));
		Assert.assertTrue(siteParsing.isUselesLine("is connected "));
		Assert.assertTrue(siteParsing.isUselesLine("said,"));
		Assert.assertTrue(siteParsing.isUselesLine("has timed out"));
		Assert.assertTrue(siteParsing.isUselesLine("joins the table at seat"));
		Assert.assertTrue(siteParsing.isUselesLine("Uncalled bet"));
		Assert.assertTrue(siteParsing.isUselesLine("has returned"));
		Assert.assertTrue(siteParsing.isUselesLine("doesn't show hand"));
		Assert.assertTrue(siteParsing.isUselesLine("was removed from the table for failing to post"));
		Assert.assertTrue(siteParsing.isUselesLine("mucks hand"));
		Assert.assertTrue(siteParsing.isUselesLine("finished the tournament in"));
		// && (!line.startsWith("Seat"));

	}

	public void testParseHandDate() {
		String nextLine = "PokerStars Hand #121171794625: Tournament #966539218, €0.46+€0.04 EUR Hold'em No Limit - Match Round I, Level III (20/40) - 2014/09/07 4:28:28 CET [2014/09/06 22:28:28 ET]";

		final ICardroomParser siteParsing = new WinamaxParsing(null);
		Date date = siteParsing.parseHandDate(nextLine);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(Calendar.SEPTEMBER, calendar.get(Calendar.MONTH));
		Assert.assertEquals(2014, calendar.get(Calendar.YEAR));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(28, calendar.get(Calendar.MINUTE));
		Assert.assertEquals(28, calendar.get(Calendar.SECOND));

		nextLine = "PokerStars Hand #121171200712: Tournament #966525607, €0.22+€0.03 EUR Hold'em No Limit - Level II (15/30) - 2014/09/07 4:00:48 CET [2014/09/06 22:00:48 ET]";
		date = siteParsing.parseHandDate(nextLine);
		calendar.setTime(date);
		Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(Calendar.SEPTEMBER, calendar.get(Calendar.MONTH));
		Assert.assertEquals(2014, calendar.get(Calendar.YEAR));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
		Assert.assertEquals(48, calendar.get(Calendar.SECOND));
	}

	@Test
	public void testFileToMap() {

		final File fichier = new File(
				"./target/test-classes/HH20130828 T780452500 Hold'em No Limit 0,89 € + 0,11 €.txt");
		final WinamaxParsing parser = new WinamaxParsing(fichier);
		final Map<String, Hand> result = parser.parsing().getHands();

		Assert.assertEquals(97, result.size());

		final Hand hand = result.get("103356159434");
		Assert.assertEquals("103356159434", hand.getLabel());
		Assert.assertEquals(1, hand.getLevel());
	}

	@Test
	public void testParseCurrency() {

		String nextLine = "PokerStars Hand #121171794625: Tournament #966539218, €0.46+€0.04 EUR Hold'em No Limit - Match Round I, Level III (20/40) - 2014/09/07 4:28:28 CET [2014/09/06 22:28:28 ET]";
		final WinamaxParsing parser = new WinamaxParsing(null);
		Assert.assertEquals(Currency.EURO, parser.parseCurrency(nextLine));

		nextLine = "PokerStars Hand #146280650631:  Hold'em No Limit ($0.02/$0.05 USD) - 2016/01/01 13:09:23 ET";
		Assert.assertEquals(Currency.USD, parser.parseCurrency(nextLine));

	}
}
