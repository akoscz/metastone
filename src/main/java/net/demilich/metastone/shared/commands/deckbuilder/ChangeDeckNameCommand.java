package net.demilich.metastone.shared.commands.deckbuilder;

import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.metastone.shared.GameNotification;

public class ChangeDeckNameCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);

		String newDeckName = (String) notification.getBody();
		deckProxy.getActiveDeck().setName(newDeckName);
	}

}
