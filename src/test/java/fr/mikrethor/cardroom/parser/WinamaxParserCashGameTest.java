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

	}

}
