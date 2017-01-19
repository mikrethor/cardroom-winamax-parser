package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.util.Date;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.InfoSession;

/**
 * Parsing Winamax mano. Fonctionne mais dur emaintenir. Piste regex en cours
 * d'etude.
 * 
 * D:\Profiles\Thor\Documents\Winamax Poker\accounts\Mikrethor\history
 * 
 * @author Thor
 * 
 */
public class WinamaxParserCashGame extends WinamaxParser implements ICardroomParser {
	public WinamaxParserCashGame(File fileToParse) {
		super(fileToParse);
	}

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WinamaxParserCashGameTest.class);

	@Override
	public String parseNewHandLine(String nextLine, Scanner input, String phase, String[] nextPhases, InfoSession game,
			Hand hand) {
		if (nextLine.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Demarrage {} -> {}", NEW_HAND, nextLine);
			}

			final double buy = parseBuyIn(nextLine);
			final double fee = parseFee(nextLine);

			final String handId = parseHandIdSite(nextLine);
			final Date handDate = parseHandDate(nextLine);
			hand.setDate(handDate.getTime());
			hand.setLabel(handId);
			game.setBuyIn(buy + fee);
			game.setFee(fee);
			// Pas de level en cash game
			hand.setLevel(0);

			final Double smallBlind = parseSmallBlind(nextLine);
			final Double bigBlind = parseBigBlind(nextLine);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("HandId :  {}, SmallBlind/BigBlind {}/{}", handId, smallBlind, bigBlind);
			}

			hand.setBigBlind(bigBlind);
			hand.setSmallBlind(smallBlind);
		}
		return nextLine;
	}

	@Override
	public String parseSummary(String nextLine, Scanner input, InfoSession game, String phase, String[] nextPhases,
			Hand hand) {
		if (nextLine.startsWith(SUMMARY)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Lecture du {} -> {}, ", SUMMARY, nextLine);
			}
			while (input.hasNext()) {
				if (nextLine.startsWith("Total pot ")) {
					final Double rake = parseRake(nextLine);
					hand.setTotalPot(parseTotalPot(nextLine));
					hand.setRake(rake);
				}

				if (nextLine.startsWith(BOARD)) {
					this.parseCards(nextLine);
				}

				if (nextLine.startsWith(SEAT) && nextLine.contains(CLOSING_SQUARE_BRACKET)) {
					this.parseCards(nextLine);
				}
				if (startsWith(nextLine, nextPhases)) {
					break;
				} else {
					nextLine = input.nextLine();
				}
			}
			hand.getActions().addAll(hand.getPreflopActions());
			hand.getActions().addAll(hand.getFlopActions());
			hand.getActions().addAll(hand.getTurnActions());
			hand.getActions().addAll(hand.getRiverActions());
			hand.getActions().addAll(hand.getShowdownActions());
			game.addHand(hand);
		}
		return nextLine;
	}

	@Override
	public Double parseBuyIn(String chaine) {
		return 0d;
	}

	@Override
	public Double parseFee(String chaine) {
		return 0d;
	}

	@Override
	public Double parseSmallBlind(String chaine) {
		int startPosition = chaine.indexOf(LEFT_PARENTHESIS) + 1;
		int endPosition = chaine.indexOf(RIGHT_PARENTHESIS);
		final String blinds = chaine.substring(startPosition, endPosition);
		String smallBlind = "";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Nb / :  {}", blinds.indexOf(SLASH));
			LOGGER.debug("Blinds {}", blinds);

		}
		if (blinds.indexOf(SLASH) != blinds.lastIndexOf(SLASH)) {
			startPosition = blinds.indexOf(SLASH) + 1;
			endPosition = blinds.lastIndexOf(SLASH);
		} else {
			startPosition = 0;
			endPosition = blinds.indexOf(SLASH);
		}
		smallBlind = blinds.substring(startPosition, endPosition);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SmallBlind {}", smallBlind);
		}
		smallBlind = removeCharacter(smallBlind, money.getSymbol());
		return Double.parseDouble(smallBlind);

	}

	private String removeCharacter(String chaine, String caractere) {
		if (chaine.contains(caractere)) {
			return chaine.replace(caractere, EMPTY);
		}
		return chaine;
	}

	@Override
	public Double parseBigBlind(String chaine) {
		int startPosition = chaine.indexOf(LEFT_PARENTHESIS) + 1;
		int endPosition = chaine.indexOf(RIGHT_PARENTHESIS);
		final String blinds = chaine.substring(startPosition, endPosition);
		String bigBlind = chaine.substring(chaine.indexOf(SLASH) + 1, chaine.indexOf(RIGHT_PARENTHESIS));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Nb / :  {}", blinds.indexOf(SLASH));
			LOGGER.debug("Blinds {}", blinds);
		}
		if (blinds.indexOf(SLASH) != blinds.lastIndexOf(SLASH)) {
			startPosition = blinds.lastIndexOf(SLASH) + 1;
			endPosition = blinds.length();
		} else {
			startPosition = blinds.indexOf(SLASH) + 1;
			endPosition = blinds.length();
		}

		bigBlind = blinds.substring(startPosition, endPosition);
		bigBlind = removeCharacter(bigBlind, money.getSymbol());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("BigBlind {}", bigBlind);
		}

		return Double.parseDouble(bigBlind);
	}

	@Override
	public String parseTableId(String chaine) {
		final int startPosition = chaine.indexOf(APOSTROPHE) + 1;
		final int endPosition = chaine.lastIndexOf(APOSTROPHE);
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public String parseGameIdSite(String chaine) {
		return parseTableId(chaine);
	}

	@Override
	public String getTournamentId() {
		// 20140709_Nice 02_real_holdem_no-limit.txt
		final String fileName = this.getFileToParse().getName();
		final String id = fileName.substring(fileName.indexOf(UNDERSCORE) + 1,
				fileName.indexOf(UNDERSCORE, fileName.indexOf(UNDERSCORE) + 1));
		return id;
	}

}
