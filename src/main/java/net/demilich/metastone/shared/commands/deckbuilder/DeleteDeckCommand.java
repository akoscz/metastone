package net.demilich.metastone.shared.commands.deckbuilder;

import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.metastone.shared.GameNotification;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;

public class DeleteDeckCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		Deck deck = (Deck) notification.getBody();
		
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);
		deckProxy.deleteDeck(deck);
	}

}
