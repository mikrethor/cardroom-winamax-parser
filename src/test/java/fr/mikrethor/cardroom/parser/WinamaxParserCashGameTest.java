package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.Domain;
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

	}

	// Winamax Poker - CashGame - HandId: #9401942-10906-1483851372 - Holdem no
	// limit (0.01€/0.02€) - 2017/01/08 04:56:12 UTC
	// Table: 'Nice' 5-max (real money) Seat #5 is the button
	// Seat 1: bilboquets (5€)
	// Seat 2: papynelson08 (1.18€)
	// Seat 3: ...Thor... (2.06€)
	// Seat 4: Moronar (2.30€)
	// Seat 5: n00bish (3.03€)
	// *** ANTE/BLINDS ***
	// bilboquets posts small blind 0.01€
	// ...Thor... posts big blind 0.02€
	// Dealt to ...Thor... [Ts 6d]
	// *** PRE-FLOP ***
	// Moronar calls 0.02€
	// n00bish raises 0.08€ to 0.10€
	// bilboquets folds
	// ...Thor... folds
	// Moronar calls 0.08€
	// *** FLOP *** [Qc Kd 5d]
	// Moronar checks
	// n00bish bets 0.15€
	// Moronar calls 0.15€
	// *** TURN *** [Qc Kd 5d][7d]
	// Moronar checks
	// n00bish checks
	// *** RIVER *** [Qc Kd 5d 7d][7s]
	// Moronar bets 0.24€
	// n00bish folds
	// Moronar collected 0.74€ from pot
	// *** SUMMARY ***
	// Total pot 0.74€ | Rake 0.03€
	// Board: [Qc Kd 5d 7d 7s]
	// Seat 4: Moronar won 0.74€

}
