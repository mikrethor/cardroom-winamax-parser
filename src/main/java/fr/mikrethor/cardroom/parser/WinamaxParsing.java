package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mikrethor.cardroom.enums.Card;
import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.EAction;
import fr.mikrethor.cardroom.enums.GameType;
import fr.mikrethor.cardroom.enums.PokerType;
import fr.mikrethor.cardroom.enums.Round;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.InfoSession;
import fr.mikrethor.cardroom.pojo.Player;
import fr.mikrethor.cardroom.utils.DateUtils;

/**
 * Parsing Winamax.
 * 
 * @author Thor
 * 
 */
public class WinamaxParsing extends CardroomFileParser implements ICardroomParser {

	public WinamaxParsing(File fileToParse) {
		super(fileToParse);
	}

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WinamaxParsing.class);

	protected static final String ANTE_BLIND = "*** ANTE/BLINDS ***";
	protected static final String PRE_FLOP = "*** PRE-FLOP ***";
	protected static final String FLOP = "*** FLOP ***";
	protected static final String TURN = "*** TURN ***";
	protected static final String RIVER = "*** RIVER ***";
	protected static final String SHOW_DOWN = "*** SHOW DOWN ***";
	protected static final String SUMMARY = "*** SUMMARY ***";
	protected static final String NEW_HAND = "Winamax Poker";
	protected static final String SEAT = "Seat";
	protected static final String BOARD = "Board";
	protected static final String ENCODING = "UTF8";
	protected static final String TABLE = "Table: ";
	protected static final String NO_RAKE = "No rake";
	protected static final String RAKE = "Rake ";
	protected static final String TOTAL_POT = "Total pot ";
	protected static final String DEALT_TO = "Dealt to ";
	protected static final String DEALT = "Dealt";
	protected static final String DENIES = "denies";
	protected static final String POSTS = "posts";
	protected static final String SMALL = "small";
	protected static final String BUY_IN = "buyIn: ";
	protected static final String LEVEL = "level:";
	protected static final String LEVEL_ESPACE = LEVEL + SPACE;
	protected static final String PLUS_ESPACE = PLUS + SPACE;
	protected static final String IS_THE_BUTTON = "is the button";
	protected static final String HANDID_HASHTAG = "HandId: #";
	protected static final String MINUS_HANDID = " - HandId:";
	protected static final String MAX = "max";

	@Override
	public Action parseAction(String chaine, Map<String, Player> players) {
		final String[] tab = chaine.split(SPACE);
		String action = "";
		String joueur = "";
		String entre = "";
		String montant = "0";
		Card[] main = null;

		for (int i = 0; i < tab.length; i++) {
			if (EAction.FOLDS.getValue().equals(tab[i]) || EAction.CALLS.getValue().equals(tab[i])
					|| EAction.RAISES.getValue().equals(tab[i]) || EAction.CHECKS.getValue().equals(tab[i])
					|| EAction.COLLECTED.getValue().equals(tab[i]) || EAction.BETS.getValue().equals(tab[i])
					|| EAction.SHOWS.getValue().equals(tab[i])) {
				joueur = "";
				action = tab[i];
				if (EAction.CALLS.getValue().equals(tab[i]) || EAction.RAISES.getValue().equals(tab[i])
						|| EAction.COLLECTED.getValue().equals(tab[i]) || EAction.BETS.getValue().equals(tab[i])) {
					montant = tab[i + 1];
					montant = montant.replace(money.getSymbol(), EMPTY);
				}

				for (int j = 0; j < i; j++) {
					if (j == 0) {
						entre = "";
					} else {
						entre = SPACE;
					}
					joueur = joueur + entre + tab[j];

				}
				if (EAction.SHOWS.getValue().equals(tab[i])) {
					main = parseCards(chaine);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" main : {}", main.toString());
					}
				}

			}
		}
		return new Action(players.get(joueur), EAction.valueOf(action.toUpperCase()), Double.parseDouble(montant), main,
				null);
	}

	public InfoSession parsing() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("L'encodage utilis� pour lire le fichier est {}.", ENCODING);
			LOGGER.debug("La devise utilisee est le {}.", money.getSymbol());
		}
		Scanner input;
		try {
			input = new Scanner(this.getFileToParse(), ENCODING);
		} catch (final FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		String nextLine = null;
		Hand hand = null;
		final InfoSession infoSession = new InfoSession();
		infoSession.setSite(cardRoom);
		// Map<String, Hand> hands = new HashMap<String, Hand>();
		boolean firstIteration = true;

		while (input.hasNext()) {
			// Sinon le parsing ne fonctionne pas
			if (firstIteration) {
				nextLine = input.nextLine();
				firstIteration = false;
			}

			// Demarrage de la lecture d'une main
			if (nextLine.startsWith(NEW_HAND)) {
				hand = new Hand();

				nextLine = parseNewHandLine(nextLine, input, NEW_HAND, null, infoSession, hand);
			}
			// Passage a la ligne suivante pour les infos TABLE (evite les tours
			// de boucle inutiles)
			nextLine = input.nextLine();

			nextLine = parseTableLine(nextLine, input, TABLE, null, infoSession, hand);
			// Passage a la ligne suivante pour les infos SEAT(evite les tours
			// de boucle inutiles)
			nextLine = input.nextLine();

			// Remise � off des joueurs
			for (final Player playerTemp : infoSession.getPlayers().values()) {
				playerTemp.setOn(false);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Desactivation de tous les joueurs pour maj... -> {}", playerTemp.getName());
				}
			}

			// TODO prendre en compte le siege favoris (affichage)
			// Attention a bien positionnela lecture des seat
			nextLine = parseSeatLine(nextLine, input, SEAT, new String[] { ANTE_BLIND }, infoSession, hand);

			nextLine = parseAntesAndBlinds(nextLine, input, ANTE_BLIND, new String[] { PRE_FLOP, SUMMARY }, infoSession,
					hand);

			nextLine = parsePreflop(nextLine, input, hand);

			nextLine = parseFlop(nextLine, input, hand);

			nextLine = parseTurn(nextLine, input, hand);

			nextLine = parseRiver(nextLine, input, hand);

			nextLine = parseShowdown(nextLine, input, hand);

			nextLine = parseSummary(nextLine, input, infoSession, SUMMARY, new String[] { NEW_HAND }, hand);

		}
		// Fermeture du scanner
		input.close();
		return infoSession;

	}

	@Override
	public String parseActionsByPhase(String nextLine, Scanner input, Hand hand, String phase, String[] nextPhases,
			List<Action> actions) {
		String nextL = nextLine;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Lecture de la phase : {}", phase);

		}
		if (nextL.startsWith(phase)) {
			// Demarrage de la lecture de la phase
			while (input.hasNext()) {
				nextL = input.nextLine();
				// Check si on tombe sur la prochaine phase
				if (startsWith(nextL, nextPhases)) {
					break;
				} else {
					// Ajout des actions ela phase dans le HanDTO
					final Action action = this.parseAction(nextL, hand.getPlayersByName());

					Round round = null;

					switch (phase) {
					case PRE_FLOP:
						round = Round.PRE_FLOP;
						break;
					case FLOP:
						round = Round.FLOP;
						break;
					case TURN:
						round = Round.TURN;
						break;
					case RIVER:
						round = Round.RIVER;
						break;
					case SHOW_DOWN:
						round = Round.SHOWDOWN;
						break;
					default:
						round = null;
					}
					action.setPhase(round);
					actions.add(action);
				}
			}

		}
		// Retourne le nextLine pour pouvoir continuer l'itteration du scanner
		// comme il faut.
		return nextL;
	}

	@Override
	public String parseNewHandLine(String nextLine, Scanner input, String phase, String[] nextPhases, InfoSession game,
			Hand hand) {
		final String nextL = nextLine;
		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Demarrage {} -> {}", NEW_HAND, nextL);
			}
			final String[] tab = nextL.split(" - ");
			double buy = 0d;
			double fee = 0d;
			int level = 0;
			// TODO decoreller les traitement pour eviter les IF.
			if (!GameType.CASH.getType().equals(tab[1])) {
				// Parsing
				buy = parseBuyIn(nextL);
				if (buy != 0) {
					fee = parseFee(nextL);
				} else {
					fee = 0;
				}
				level = parseLevel(nextL);
				// Logging
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Buy in : {}, Fee : {}, Level : {}", buy, fee, level);
				}
			}
			final String handId = parseHandIdSite(nextL);
			hand.setLabel(handId);
			game.setBuyIn(buy + fee);
			game.setFee(fee);
			hand.setLevel(level);
			final Date handDate = parseHandDate(nextL);
			hand.setDate(handDate.getTime());
			final Double smallBlind = parseSmallBlind(nextL);
			final Double bigBlind = parseBigBlind(nextL);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("HandId :  {}, SmallBlind/BigBlind {}/{}", handId, smallBlind, bigBlind);
			}
			hand.setBigBlind(bigBlind);
			hand.setSmallBlind(smallBlind);
		}
		return nextL;
	}

	@Override
	public String parseTableLine(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession infoSession, Hand hand) {
		final String nextL = nextLine;
		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Lecture de la {} -> {}, ", TABLE, nextL);
			}
			final String tableId = parseTableId(nextL);

			final String gameIdSite = parseGameIdSite(nextL);
			final String numberOfPlayerByTable = parseNumberOfPlayerByTable(nextL);
			final Integer buttonSeat = parseButtonSeat(nextL);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GameId : {},TableId : {}, NbPlayerByTable : {}, ButtonSeat : {}", gameIdSite, tableId,
						numberOfPlayerByTable, buttonSeat);
			}
			infoSession.setLabel(gameIdSite);
			infoSession.setNbPlayersOnOneTable(Integer.parseInt(numberOfPlayerByTable));
			infoSession.setPokerType(PokerType.HOLDEM);
			hand.setIdTable(tableId);
			hand.setButtonSeat(buttonSeat);

		}
		return nextL;
	}

	@Override
	public String parseSeatLine(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession infoSession, Hand hand) {
		String nextL = nextLine;
		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Lecture du {} -> {}, ", SEAT, nextL);
			}
			int i = 0;
			while (input.hasNext()) {
				i++;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("main : {}", i);
				}

				final Player playerInGame = parsePlayerSeat(nextL);

				hand.addPlayer(playerInGame);

				infoSession.getPlayers().put(playerInGame.getName(), playerInGame);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Button Seat is {} and PlayerSeat is {}.", hand.getButtonSeat(),
							playerInGame.getSeat());
				}
				/**
				 * @TODO mieux gerer le cas ou le button a eteeliminer au tour
				 *       d'avant.
				 */
				if (hand.getButtonSeat().equals(playerInGame.getSeat())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Button Player is {} in seat {}.", playerInGame.getName(), playerInGame.getSeat());
					}
					hand.setDealerPlayer(playerInGame);
				}

				nextL = input.nextLine();
				if (startsWith(nextL, nextPhases)) {
					break;
				}
			}
		}
		return nextL;
	}

	public String parsePreflop(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, PRE_FLOP, new String[] { FLOP, SUMMARY },
				hand.getPreflopActions());
	}

	public String parseFlop(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, FLOP, new String[] { TURN, SUMMARY }, hand.getFlopActions());
	}

	public String parseTurn(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, TURN, new String[] { RIVER, SUMMARY }, hand.getTurnActions());
	}

	public String parseRiver(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, RIVER, new String[] { SHOW_DOWN, SUMMARY },
				hand.getRiverActions());
	}

	public String parseShowdown(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, SHOW_DOWN, new String[] { SUMMARY },
				hand.getShowdownActions());
	}

	public String parseSummary(String nextLine, Scanner input, InfoSession session, String phase, String[] nextPhases,
			Hand hand) {
		String nextL = nextLine;
		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Lecture du {} -> {}, ", SUMMARY, nextL);
			}
			while (input.hasNext()) {
				// Total pot 180 | No rake
				if (nextL.startsWith(TOTAL_POT)) {
					final Double rake = parseRake(nextL);
					hand.setTotalPot(parseTotalPot(nextL));
					hand.setRake(rake);
				}
				if (nextL.startsWith(BOARD)) {
					this.parseCards(nextL);
				}
				if (nextL.startsWith(SEAT) && nextL.contains(CLOSING_SQUARE_BRACKET)) {
					this.parseCards(nextL);
				}
				if (startsWith(nextL, nextPhases)) {
					break;
				} else {
					nextL = input.nextLine();
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Main: {}, {}", hand.toString());
			}

			hand.getActions().addAll(hand.getPreflopActions());
			hand.getActions().addAll(hand.getFlopActions());
			hand.getActions().addAll(hand.getTurnActions());
			hand.getActions().addAll(hand.getRiverActions());
			hand.getActions().addAll(hand.getShowdownActions());
			hand.setInfoSession(session);
			session.addHand(hand);

		}
		return nextL;
	}

	@Override
	public String parseAntesAndBlinds(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession game, Hand hand) {
		String nextL = nextLine;
		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Lecture du {} -> {}, ", phase, nextL);
			}
			while (input.hasNext()) {
				nextL = input.nextLine();
				if (nextL.startsWith(DEALT)) {
					final String playerAccount = parsePlayerAccount(nextL);
					final Player playerDealt = hand.getPlayersByName().get(playerAccount);
					playerDealt.setCards(this.parseCards(nextL));
					hand.setPlayer(playerDealt);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Player {}, ", playerAccount);
					}

				} else {
					if (startsWith(nextL, nextPhases)) {
						break;
					}
					final String[] tab = nextL.split(SPACE);
					final String blind = tab[tab.length - 3];

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("nextLine {}.", nextL);
						LOGGER.debug("blind {}.", blind);
					}
					// cas ou le joueur ne paie pas la big blind
					if (DENIES.equals(blind)) {
						continue;
					}
					String joueur;
					if (POSTS.equals(blind)) {
						joueur = tab[0];
					} else {
						joueur = this.getPlayerBlind(tab);
						if (SMALL.equals(blind)) {
							hand.setSmallBlindPlayer(hand.getPlayers().get(joueur));
						} else {
							hand.setBigBlindPlayer(hand.getPlayersByName().get(joueur));
						}
					}
				}
			}
		}
		return nextL;
	}

	@Override
	public Double parseBuyIn(String chaine) {
		int startPosition = chaine.indexOf(BUY_IN) + BUY_IN.length();
		int endPosition = chaine.indexOf(LEVEL);

		String buyIn = chaine.substring(startPosition, endPosition);
		LOGGER.info(buyIn);
		// Cas 1: 0,90eee+ 0,10e
		// Cas 2: Ticket only
		if (buyIn.contains(PLUS)) {
			startPosition = 0;
			if (buyIn.contains(money.getSymbol())) {
				endPosition = buyIn.indexOf(money.getSymbol());
			} else {
				endPosition = buyIn.indexOf(PLUS);
			}
			LOGGER.info(buyIn);
			buyIn = buyIn.substring(startPosition, endPosition);
			buyIn = buyIn.replace(COMMA, DOT);
			buyIn = buyIn.replace(money.getSymbol(), EMPTY);
			return Double.parseDouble(buyIn);
		}
		return 0d;
	}

	@Override
	public Double parseFee(String chaine) {
		final String[] tab = chaine.split(" - ");
		int startPosition = tab[1].indexOf(BUY_IN) + BUY_IN.length();
		int endPosition = tab[1].indexOf(LEVEL);

		String fee = tab[1].substring(startPosition, endPosition);
		// Cas 1: 0,90eee+ 0,10e
		// Cas 2: Ticket only
		if (fee.contains(PLUS)) {
			startPosition = fee.indexOf(PLUS_ESPACE) + PLUS_ESPACE.length();
			if (fee.contains(money.getSymbol())) {
				endPosition = fee.lastIndexOf(money.getSymbol());
				LOGGER.info("devise" + fee);
			} else {
				endPosition = fee.length();
				LOGGER.info("pas devise" + fee);
			}
			LOGGER.info("fee: {}, {}, {}", fee, startPosition, endPosition);
			fee = fee.substring(startPosition, endPosition);
			fee = fee.replace(COMMA, DOT);
			return Double.parseDouble(fee);
		}
		return 0d;
	}

	@Override
	public int parseLevel(String chaine) {
		final int startPosition = chaine.indexOf(LEVEL_ESPACE) + LEVEL_ESPACE.length();
		final int endPosition = chaine.indexOf(MINUS_HANDID);
		return Integer.parseInt(chaine.substring(startPosition, endPosition));
	}

	@Override
	public String parseHandIdSite(String chaine) {
		final int startPosition = chaine.indexOf(HANDID_HASHTAG) + HANDID_HASHTAG.length();
		final int endPosition = chaine.indexOf(DASH,
				chaine.indexOf(DASH, chaine.indexOf(HANDID_HASHTAG) + HANDID_HASHTAG.length()) + 1);
		return chaine.substring(startPosition, endPosition);
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
		return Double.parseDouble(smallBlind);

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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("BigBlind {}", bigBlind);
		}
		return Double.parseDouble(bigBlind);
	}

	@Override
	public String parseTableId(String chaine) {
		final int startPosition = chaine.indexOf(HASHTAG) + 1;
		final int endPosition = chaine.lastIndexOf(APOSTROPHE);
		LOGGER.info("startPosition {}, endPosition {}.", startPosition, endPosition);
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public String parseNumberOfPlayerByTable(String chaine) {
		final int startPosition = chaine.lastIndexOf(APOSTROPHE) + 2;
		final int endPosition = chaine.lastIndexOf(MAX) - 1;
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public String parseGameIdSite(String chaine) {
		final int startPosition = chaine.indexOf(LEFT_PARENTHESIS) + 1;
		final int endPosition = chaine.indexOf(RIGHT_PARENTHESIS, startPosition);
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public Integer parseButtonSeat(String chaine) {
		final int startPosition = chaine.lastIndexOf(HASHTAG) + 1;
		final int endPosition = chaine.indexOf(IS_THE_BUTTON) - 1;
		return Integer.parseInt(chaine.substring(startPosition, endPosition));
	}

	@Override
	public Player parsePlayerSeat(String chaine) {
		final int espace = chaine.indexOf(SPACE);
		final int deuxpoints = chaine.indexOf(COLON);
		final int parenthesegauche = chaine.indexOf(LEFT_PARENTHESIS);
		final int parenthesedroite = chaine.indexOf(RIGHT_PARENTHESIS);

		final String seat = chaine.substring(espace + 1, deuxpoints);
		final String playerName = chaine.substring(deuxpoints + 2, parenthesegauche - 1);
		String stack = chaine.substring(parenthesegauche + 1, parenthesedroite);
		stack = stack.replace(money.getSymbol(), EMPTY);
		final Player player = new Player(cardRoom, playerName);

		player.setStack(Double.parseDouble(stack));
		player.setSeat(Integer.parseInt(seat));
		player.setOn(true);
		return player;
	}

	@Override
	public Double parseTotalPot(String chaine) {
		final int startPosition = TOTAL_POT.length();
		final int endPosition = chaine.indexOf(PIPE) - 1;
		chaine = chaine.substring(startPosition, endPosition);
		chaine = chaine.replace(money.getSymbol(), EMPTY);
		return Double.parseDouble(chaine);
	}

	@Override
	public Double parseRake(String chaine) {
		final int startPosition = chaine.indexOf(PIPE) + 2;
		final int endPosition = chaine.length();
		String sub = chaine.substring(startPosition, endPosition);
		if (NO_RAKE.equals(sub)) {
			return 0d;
		} else {
			sub = sub.replace(money.getSymbol(), EMPTY);
			sub = sub.substring(RAKE.length(), sub.length());
			return Double.parseDouble(sub);
		}
	}

	@Override
	public String parsePlayerAccount(String chaine) {

		final int startPosition = DEALT_TO.length();
		final int endPosition = chaine.lastIndexOf(OPENNING_SQUARE_BRACKET) - 1;
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public String getTournamentId() {
		final String fileName = this.getFileToParse().getName();
		String id;
		if (fileName.contains(LEFT_PARENTHESIS)) {
			id = fileName.substring(fileName.indexOf(LEFT_PARENTHESIS) + 1, fileName.indexOf(RIGHT_PARENTHESIS));
		} else {
			id = fileName.substring(fileName.indexOf(UNDERSCORE) + 1,
					fileName.indexOf(UNDERSCORE, fileName.indexOf(UNDERSCORE) + 1));
		}
		return id;
	}

	@Override
	public GameType getGameTypeFromFilename(String fileName) {
		if (EMPTY.equals(fileName) || fileName == null) {
			return null;
		} else {
			if (fileName.contains(LEFT_PARENTHESIS)) {
				return GameType.TOURNAMENT;
			}
			return GameType.CASH;
		}
	}

	@Override
	public Date parseHandDate(String chaine) {
		final int startPosition = chaine.lastIndexOf(DASH) + 2;
		final int endPosition = chaine.lastIndexOf(SPACE);
		chaine = chaine.substring(startPosition, endPosition);
		try {
			return DateUtils.toDate(chaine, "yyyy/MM/dd HH:mm:ss");
		} catch (final ParseException e) {
			LOGGER.error(e.getMessage(), e);
			return new Date();
		}
	}

	@Override
	public String parseDealer(String nextLine, Scanner input, String phase, String[] nextPhases, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Currency parseCurrency(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isUselesLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, StringBuffer> fileToMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hand textToHandDto(StringBuffer text) {
		// TODO Auto-generated method stub
		return null;
	}

}
