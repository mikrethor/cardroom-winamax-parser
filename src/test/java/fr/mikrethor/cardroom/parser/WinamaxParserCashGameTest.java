package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.mikrethor.cardroom.enums.Card;
import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.Domain;
import fr.mikrethor.cardroom.enums.EAction;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Cardroom;
import fr.mikrethor.cardroom.pojo.Hand;

public class WinamaxParserCashGameTest {
	@Test
	public void parsingCashGameFile() {
		final String fileName = "./target/test-classes/20170107_Nice_real_holdem_no-limit.txt";
		final File file = new File(fileName);
		final WinamaxParserCashGame siteParsing = new WinamaxParserCashGame(file);
		siteParsing.setCardroom(new Cardroom("Winamax", Domain.FR));
		siteParsing.setCurrency(Currency.EURO);
		Map<String, Hand> hands = null;

		Assert.assertEquals(true, file.exists());
		Assert.assertEquals(true, file.isFile());

		hands = siteParsing.parse();

		Assert.assertNotNull(hands);

		Assert.assertEquals(11, hands.values().size());

		Hand hand = hands.get("9401942-10906-1483851372");

		Assert.assertNotNull(hand);

		Assert.assertEquals(Currency.EURO, hand.getCurrency());
		Assert.assertEquals(siteParsing.getCardroom(), hand.getCardRoom());

		Assert.assertEquals(Double.valueOf(0.74), Double.valueOf(hand.getTotalPot()));
		Assert.assertEquals(Double.valueOf(0.03), Double.valueOf(hand.getRake()));

		Assert.assertEquals("...Thor...", hand.getBigBlindPlayer().getName());
		Assert.assertEquals("bilboquets", hand.getSmallBlindPlayer().getName());
		Assert.assertEquals("...Thor...", hand.getPlayer().getName());
		Assert.assertEquals("n00bish", hand.getDealerPlayer().getName());

		final Card[] flop = hand.getFlop();
		Assert.assertEquals(siteParsing.stringToECards("Qc"), flop[0]);
		Assert.assertEquals(new WinamaxParser(null).stringToECards("Kd"), flop[1]);
		Assert.assertEquals(new WinamaxParser(null).stringToECards("5d"), flop[2]);

		final Card turn = hand.getTurn();
		Assert.assertEquals(siteParsing.stringToECards("7d"), turn);

		final Card river = hand.getRiver();
		Assert.assertEquals(siteParsing.stringToECards("7s"), river);

		List<Action> preflopActions = hand.getPreflopActions();
		Assert.assertEquals(5, preflopActions.size());

		Assert.assertEquals("Moronar", preflopActions.get(0).getPlayer().getName());
		Assert.assertEquals(EAction.CALLS, preflopActions.get(0).getAction());
		Assert.assertEquals(Double.valueOf(0.02), Double.valueOf(preflopActions.get(0).getMontant()));

		Assert.assertEquals("n00bish", preflopActions.get(1).getPlayer().getName());
		Assert.assertEquals(EAction.RAISES, preflopActions.get(1).getAction());
		Assert.assertEquals(Double.valueOf(0.08), Double.valueOf(preflopActions.get(1).getMontant()));

		Assert.assertEquals("bilboquets", preflopActions.get(2).getPlayer().getName());
		Assert.assertEquals(EAction.FOLDS, preflopActions.get(2).getAction());

		Assert.assertEquals("...Thor...", preflopActions.get(3).getPlayer().getName());
		Assert.assertEquals(EAction.FOLDS, preflopActions.get(3).getAction());

		List<Action> flopActions = hand.getFlopActions();
		Assert.assertEquals(3, flopActions.size());

		Assert.assertEquals("Moronar", flopActions.get(0).getPlayer().getName());
		Assert.assertEquals(EAction.CHECKS, flopActions.get(0).getAction());

		Assert.assertEquals("n00bish", flopActions.get(1).getPlayer().getName());
		Assert.assertEquals(EAction.BETS, flopActions.get(1).getAction());
		Assert.assertEquals(Double.valueOf(0.15), Double.valueOf(flopActions.get(1).getMontant()));

		Assert.assertEquals("Moronar", flopActions.get(2).getPlayer().getName());
		Assert.assertEquals(EAction.CALLS, flopActions.get(2).getAction());
		Assert.assertEquals(Double.valueOf(0.15), Double.valueOf(flopActions.get(1).getMontant()));

		List<Action> turnActions = hand.getTurnActions();
		Assert.assertEquals(2, turnActions.size());

		Assert.assertEquals("Moronar", turnActions.get(0).getPlayer().getName());
		Assert.assertEquals(EAction.CHECKS, turnActions.get(0).getAction());

		Assert.assertEquals("n00bish", turnActions.get(1).getPlayer().getName());
		Assert.assertEquals(EAction.CHECKS, turnActions.get(1).getAction());

		List<Action> riverActions = hand.getRiverActions();
		Assert.assertEquals(3, riverActions.size());

		Assert.assertEquals("Moronar", riverActions.get(0).getPlayer().getName());
		Assert.assertEquals(EAction.BETS, riverActions.get(0).getAction());
		Assert.assertEquals(Double.valueOf(0.24), Double.valueOf(riverActions.get(0).getMontant()));

		Assert.assertEquals("n00bish", riverActions.get(1).getPlayer().getName());
		Assert.assertEquals(EAction.FOLDS, riverActions.get(1).getAction());

		Assert.assertEquals("Moronar", riverActions.get(2).getPlayer().getName());
		Assert.assertEquals(EAction.COLLECTED, riverActions.get(2).getAction());
		Assert.assertEquals(Double.valueOf(0.74), Double.valueOf(riverActions.get(2).getMontant()));
		
		Assert.assertEquals(0,hand.getShowdownActions().size());
	}

}
