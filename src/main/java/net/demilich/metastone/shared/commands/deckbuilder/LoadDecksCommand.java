package net.demilich.metastone.shared.commands.deckbuilder;

import java.io.FileNotFoundException;

import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.metastone.shared.GameNotification;

public class LoadDecksCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);

		try {
			deckProxy.loadDecks();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		getFacade().sendNotification(GameNotification.DECKS_LOADED, deckProxy.getDecks());
	}

}
