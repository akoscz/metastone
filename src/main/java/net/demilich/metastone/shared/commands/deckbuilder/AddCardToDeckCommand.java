package net.demilich.metastone.shared.commands.deckbuilder;

import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.metastone.shared.GameNotification;
import net.demilich.metastone.game.cards.Card;

public class AddCardToDeckCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);

		Card card = (Card) notification.getBody();
		if (deckProxy.addCardToDeck(card)) {
			getFacade().sendNotification(GameNotification.ACTIVE_DECK_CHANGED, deckProxy.getActiveDeck());
		}
	}

}
