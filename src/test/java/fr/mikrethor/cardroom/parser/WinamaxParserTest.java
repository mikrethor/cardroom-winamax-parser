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
import fr.mikrethor.cardroom.enums.Domain;
import fr.mikrethor.cardroom.enums.EAction;
import fr.mikrethor.cardroom.enums.GameType;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Cardroom;
import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.Player;

public class WinamaxParserTest {

	@Test
	public void testGetCards() {
		final String nextLine = "9h Ks As";
		final WinamaxParser siteParsing = new WinamaxParser(null);
		final String[] tab = siteParsing.getCards(nextLine);
		assertStringTab(new String[] { "9h", "Ks", "As" }, tab);

	}

	@Test
	public void testToCards() {
		final String nextLine = "9h Ks As";
		final WinamaxParser siteParsing = new WinamaxParser(null);
		final String[] tab = siteParsing.getCards(nextLine);
		final Card[] tabCard = siteParsing.toECards(tab);
		assertCardTab(new Card[] { Card.C_9H, Card.C_KS, Card.C_AS }, tabCard);

	}

	@Test
	public void testStringToCards() {
		final String nextLine = "9h";
		final WinamaxParser siteParsing = new WinamaxParser(null);
		final Card eCard = siteParsing.stringToECards(nextLine);
		Assert.assertEquals(Card.C_9H, eCard);

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
		final WinamaxParser siteParsing = new WinamaxParser(file);
		siteParsing.setCardroom(new Cardroom("Winamax", Domain.FR));
		Map<String, Hand> hands = null;

		Assert.assertEquals(true, file.exists());
		Assert.assertEquals(true, file.isFile());

		hands = siteParsing.parse();

		Assert.assertNotNull(hands);

		Assert.assertEquals(112, hands.values().size());
		// Check fisrt hand
		String handId = "776052078731591685-3";
		Hand main1 = hands.get(handId);
		Assert.assertEquals(6, main1.getNbPlayersOnOneTable());

		final Player schmorbauner = main1.getPlayers().get(1);
		final Player gogoyubari = main1.getPlayers().get(2);
		final Player brocoline = main1.getPlayers().get(3);
		final Player thor = main1.getPlayers().get(4);
		final Player dadsoon = main1.getPlayers().get(5);
		final Player iLoveFederer = main1.getPlayers().get(6);

		//
		// Check players seats
		Assert.assertEquals(Integer.valueOf(1), schmorbauner.getSeat());
		Assert.assertEquals(Integer.valueOf(2), gogoyubari.getSeat());
		Assert.assertEquals(Integer.valueOf(3), brocoline.getSeat());
		Assert.assertEquals(Integer.valueOf(4), thor.getSeat());
		Assert.assertEquals(Integer.valueOf(5), dadsoon.getSeat());
		Assert.assertEquals(Integer.valueOf(6), iLoveFederer.getSeat());

		// Check players names
		Assert.assertEquals("Schmorbauner", schmorbauner.getName());
		Assert.assertEquals("Gogo yubari", gogoyubari.getName());
		Assert.assertEquals("Brocoline", brocoline.getName());
		Assert.assertEquals("...Thor...", thor.getName());
		Assert.assertEquals("Dad-Soon", dadsoon.getName());
		Assert.assertEquals("iLoveFederer", iLoveFederer.getName());

		// // Check last player stack
		Assert.assertEquals(new Double(19950), new Double(schmorbauner.getStack()));
		Assert.assertEquals(new Double(19850), new Double(gogoyubari.getStack()));
		Assert.assertEquals(new Double(23050), new Double(brocoline.getStack()));
		Assert.assertEquals(new Double(19150), new Double(thor.getStack()));
		Assert.assertEquals(new Double(16700), new Double(dadsoon.getStack()));
		Assert.assertEquals(new Double(21300), new Double(iLoveFederer.getStack()));

		Assert.assertEquals(handId, main1.getLabel());

		Assert.assertEquals(Double.valueOf(100), Double.valueOf(main1.getBigBlind()));
		Assert.assertEquals(Double.valueOf(50), Double.valueOf(main1.getSmallBlind()));

		Assert.assertEquals(thor, main1.getBigBlindPlayer());
		Assert.assertEquals(brocoline, main1.getSmallBlindPlayer());
		Assert.assertEquals(thor, main1.getPlayer());
		Assert.assertEquals(gogoyubari, main1.getDealerPlayer());

		Assert.assertEquals("004", main1.getIdTable());
		Assert.assertEquals(Double.valueOf(0), Double.valueOf(main1.getRake()));

		Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(main1.getLevel()));

		// [5s 9h 5h]
		final Card[] flop = main1.getFlop();
		Assert.assertEquals(siteParsing.stringToECards("5s"), flop[0]);
		Assert.assertEquals(new WinamaxParser(null).stringToECards("9h"), flop[1]);
		Assert.assertEquals(new WinamaxParser(null).stringToECards("5h"), flop[2]);

		// Pas de turn dans le fichier
		final Card turn = main1.getTurn();
		Assert.assertEquals(Card.C_QC, turn);

		// Pas de river dans le fichier
		final Card river = main1.getRiver();
		Assert.assertEquals(Card.C_6D, river);

		Assert.assertEquals(Double.valueOf(9300), Double.valueOf(main1.getTotalPot()));

		Assert.assertEquals(Integer.valueOf(6), Integer.valueOf(main1.getPreflopActions().size()));

		final List<Action> listPreFlopActions = main1.getPreflopActions();

		Assert.assertEquals(dadsoon, listPreFlopActions.get(0).getPlayer());
		Assert.assertEquals(EAction.RAISES, listPreFlopActions.get(0).getAction());
		Assert.assertEquals(Double.valueOf(100), Double.valueOf(listPreFlopActions.get(0).getMontant()));

		Assert.assertEquals(iLoveFederer, listPreFlopActions.get(1).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(1).getAction());

		Assert.assertEquals(schmorbauner, listPreFlopActions.get(2).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(2).getAction());

		Assert.assertEquals(gogoyubari, listPreFlopActions.get(3).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(3).getAction());

		Assert.assertEquals(brocoline, listPreFlopActions.get(4).getPlayer());
		Assert.assertEquals(EAction.CALLS, listPreFlopActions.get(4).getAction());
		Assert.assertEquals(Double.valueOf(150), Double.valueOf(listPreFlopActions.get(4).getMontant()));

		Assert.assertEquals(thor, listPreFlopActions.get(5).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listPreFlopActions.get(5).getAction());

		Assert.assertEquals(Integer.valueOf(3), Integer.valueOf(main1.getFlopActions().size()));

		final List<Action> listFlopActions = main1.getFlopActions();

		Assert.assertEquals(brocoline, listFlopActions.get(0).getPlayer());
		Assert.assertEquals(EAction.CHECKS, listFlopActions.get(0).getAction());
		Assert.assertEquals(dadsoon, listFlopActions.get(1).getPlayer());
		Assert.assertEquals(EAction.BETS, listFlopActions.get(1).getAction());
		Assert.assertEquals(brocoline, listFlopActions.get(2).getPlayer());
		Assert.assertEquals(EAction.CALLS, listFlopActions.get(2).getAction());

		Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(main1.getTurnActions().size()));

		final List<Action> listTurnActions = main1.getTurnActions();

		Assert.assertEquals(brocoline, listTurnActions.get(0).getPlayer());
		Assert.assertEquals(EAction.CHECKS, listTurnActions.get(0).getAction());
		Assert.assertEquals(dadsoon, listTurnActions.get(1).getPlayer());
		Assert.assertEquals(EAction.BETS, listTurnActions.get(1).getAction());
		Assert.assertEquals(brocoline, listTurnActions.get(2).getPlayer());
		Assert.assertEquals(EAction.RAISES, listTurnActions.get(2).getAction());
		Assert.assertEquals(dadsoon, listTurnActions.get(3).getPlayer());
		Assert.assertEquals(EAction.CALLS, listTurnActions.get(3).getAction());

		Assert.assertEquals(Integer.valueOf(3), Integer.valueOf(main1.getRiverActions().size()));

		final List<Action> listRiverActions = main1.getRiverActions();

		Assert.assertEquals(brocoline, listRiverActions.get(0).getPlayer());
		Assert.assertEquals(EAction.BETS, listRiverActions.get(0).getAction());
		Assert.assertEquals(dadsoon, listRiverActions.get(1).getPlayer());
		Assert.assertEquals(EAction.FOLDS, listRiverActions.get(1).getAction());
		Assert.assertEquals(brocoline, listRiverActions.get(2).getPlayer());
		Assert.assertEquals(EAction.COLLECTED, listRiverActions.get(2).getAction());
		Assert.assertEquals(Double.valueOf(9300), Double.valueOf(listRiverActions.get(2).getMontant()));

		Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(main1.getShowdownActions().size()));
	}

	@Test

	public void testParseTableId() {
		String nextLine = "Table: 'NOCTAMBULE(180688705)#004' 6-max (real money) Seat #1 is the button";
		final WinamaxParser siteParsing = new WinamaxParser(null);

		Assert.assertEquals("004", siteParsing.parseTableId(nextLine));
		nextLine = "Table: 'Freeroll(180688949)#0239' 6-max (real money) Seat #1 is the button";
		Assert.assertEquals("0239", siteParsing.parseTableId(nextLine));
	}

	@Test

	public void testParseLevel() {
		// Cas heads-up with rebuy
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final WinamaxParser siteParsing = new WinamaxParser(null);

		Assert.assertEquals(1, siteParsing.parseLevel(nextLine));
		nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 10 - HandId: #776052078731591685-96-1483840063 - Holdem no limit (100/500/1000) - 2017/01/08 01:47:43 UTC";
		Assert.assertEquals(10, siteParsing.parseLevel(nextLine));

	}

	@Test
	public void testSetDevise() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(9), siteParsing.parseBuyIn(nextLine));
		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";
		siteParsing.setCurrency(Currency.USD);
		try {
			Assert.assertEquals(Double.valueOf(0), siteParsing.parseBuyIn(nextLine));
			Assert.fail();
		} catch (Exception e) {
			// AN error occur when the devise is not the one of the file parsed
		}
	}

	@Test
	public void testParseBuyIn() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(9), siteParsing.parseBuyIn(nextLine));
		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(0), siteParsing.parseBuyIn(nextLine));
	}

	@Test
	public void testParseFee() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";

		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(1.0), siteParsing.parseFee(nextLine));
		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";

		Assert.assertEquals(Double.valueOf(0.0), siteParsing.parseFee(nextLine));
	}

	@Test
	public void testParseHandIdSite() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";

		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals("776052078731591685-1", siteParsing.parseHandIdSite(nextLine));
		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";

		Assert.assertEquals("776053126703612144-1", siteParsing.parseHandIdSite(nextLine));
	}

	@Test
	public void testParseSmallBlind() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(50), siteParsing.parseSmallBlind(nextLine));
		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";
		Assert.assertEquals(Double.valueOf(10), siteParsing.parseSmallBlind(nextLine));
	}

	@Test
	public void testParseBigBlind() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(100), siteParsing.parseBigBlind(nextLine));
		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";
		Assert.assertEquals(Double.valueOf(20), siteParsing.parseBigBlind(nextLine));
	}

	@Test
	public void testParseNumberOfPlayerByTable() {
		String nextLine = "Table '945696315 1' 9-max Seat #1 is the button";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals("9", siteParsing.parseNumberOfPlayerByTable(nextLine));
		nextLine = "Table '939130332 1' 9-max Seat #1 is the button";
		Assert.assertEquals("9", siteParsing.parseNumberOfPlayerByTable(nextLine));
	}

	@Test
	public void testParseGameIdSite() {

		String nextLine = "Table: 'NOCTAMBULE(180688705)#004' 6-max (real money) Seat #6 is the button";

		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals("180688705", siteParsing.parseGameIdSite(nextLine));
		nextLine = "Table: 'Freeroll(180688949)#0239' 6-max (real money) Seat #1 is the button";

		Assert.assertEquals("180688949", siteParsing.parseGameIdSite(nextLine));
	}

	@Test
	public void testParseButtonSeat() {
		String nextLine = "Table '945696315 1' 9-max Seat #1 is the button";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Integer.valueOf(1), siteParsing.parseButtonSeat(nextLine));
		nextLine = "Table '939130332 1' 9-max Seat #9 is the button";

		Assert.assertEquals(Integer.valueOf(9), siteParsing.parseButtonSeat(nextLine));

	}

	@Test
	public void testParsePlayerSeat() {
		String nextLine = "Seat 1: Schmorbauner (19950)";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCardroom(new Cardroom("Winamax", Domain.FR));
		Assert.assertEquals("Schmorbauner", siteParsing.parsePlayerSeat(nextLine).getName());
		nextLine = "Seat 5: n00bish (645465465465)";
		Assert.assertEquals("n00bish", siteParsing.parsePlayerSeat(nextLine).getName());

	}

	@Test
	public void testParseTotalPot() {
		String nextLine = "Total pot 2150 | No rake";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(2150), siteParsing.parseTotalPot(nextLine));

	}

	@Test
	public void testParseRake() {
		String nextLine = "Total pot 2150 | No rake";
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		Assert.assertEquals(Double.valueOf(0), siteParsing.parseRake(nextLine));

	}

	@Test
	public void testParsePlayerAccount() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testGetGameTypeFromFilename() {
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.EURO);
		String fileName = "20170107_NOCTAMBULE(180688705)_real_holdem_no-limit.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "20170107_Freeroll(180688949)_real_omaha_pot-limit.txt";
		Assert.assertEquals(GameType.TOURNAMENT, siteParsing.getGameTypeFromFilename(fileName));
		fileName = "20170107_Nice_real_holdem_no-limit.txt";
		Assert.assertEquals(GameType.CASH, siteParsing.getGameTypeFromFilename(fileName));
	}

	@Test
	public void testIsUselesLine() {
		final ICardroomParser siteParsing = new WinamaxParser(null);
		siteParsing.setCurrency(Currency.USD);

		Assert.assertFalse(siteParsing.isUselesLine("will be allowed to play after the button"));
		Assert.assertFalse(siteParsing.isUselesLine("posts small & big blinds"));
		Assert.assertFalse(siteParsing.isUselesLine("posts the ante"));
		Assert.assertFalse(siteParsing.isUselesLine("sits out "));
		Assert.assertFalse(siteParsing.isUselesLine("leaves the table"));
		Assert.assertFalse(siteParsing.isUselesLine("is sitting out"));
		Assert.assertFalse(siteParsing.isUselesLine("is disconnected "));
		Assert.assertFalse(siteParsing.isUselesLine("is connected "));
		Assert.assertFalse(siteParsing.isUselesLine("said,"));
		Assert.assertFalse(siteParsing.isUselesLine("has timed out"));
		Assert.assertFalse(siteParsing.isUselesLine("joins the table at seat"));
		Assert.assertFalse(siteParsing.isUselesLine("Uncalled bet"));
		Assert.assertFalse(siteParsing.isUselesLine("has returned"));
		Assert.assertFalse(siteParsing.isUselesLine("doesn't show hand"));
		Assert.assertFalse(siteParsing.isUselesLine("was removed from the table for failing to post"));
		Assert.assertFalse(siteParsing.isUselesLine("mucks hand"));
		Assert.assertFalse(siteParsing.isUselesLine("finished the tournament in"));
	}

	public void testParseHandDate() {
		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";

		final ICardroomParser siteParsing = new WinamaxParser(null);
		Date date = siteParsing.parseHandDate(nextLine);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(Calendar.SEPTEMBER, calendar.get(Calendar.MONTH));
		Assert.assertEquals(2014, calendar.get(Calendar.YEAR));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(28, calendar.get(Calendar.MINUTE));
		Assert.assertEquals(28, calendar.get(Calendar.SECOND));

		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";
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

		final File fichier = new File("./target/test-classes/20170107_NOCTAMBULE(180688705)_real_holdem_no-limit.txt");
		final WinamaxParser parser = new WinamaxParser(fichier);
		parser.setCardroom(new Cardroom("Winamax", Domain.FR));
		final Map<String, Hand> result = parser.parsing().getHands();

		Assert.assertEquals(112, result.size());

		final Hand hand = result.get("776052078731591685-2");
		Assert.assertNotNull("Hand not found", hand);
		Assert.assertEquals("776052078731591685-2", hand.getLabel());
		Assert.assertEquals(1, hand.getLevel());
	}

	@Test
	public void testParseCurrency() {

		String nextLine = "Winamax Poker - Tournament \"NOCTAMBULE\" buyIn: 9€ + 1€ level: 1 - HandId: #776052078731591685-1-1483835405 - Holdem no limit (50/100) - 2017/01/08 00:30:05 UTC";
		final WinamaxParser parser = new WinamaxParser(null);
		Assert.assertEquals(Currency.EURO, parser.parseCurrency(nextLine));

		nextLine = "Winamax Poker - Tournament \"Freeroll\" buyIn: 0€ + 0€ level: 1 - HandId: #776053126703612144-1-1483812929 - Omaha pot limit (10/20) - 2017/01/07 18:15:29 UTC";
		Assert.assertEquals(Currency.EURO, parser.parseCurrency(nextLine));

	}
}
