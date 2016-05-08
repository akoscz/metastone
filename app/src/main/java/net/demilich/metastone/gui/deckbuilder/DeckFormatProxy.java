package net.demilich.metastone.gui.deckbuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.demilich.metastone.game.decks.DeckCatalogue;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.nittygrittymvc.Proxy;

public class DeckFormatProxy extends Proxy<GameNotification> {

	public static final String NAME = "DeckFormatProxy";

	private final List<DeckFormat> deckFormats = new ArrayList<DeckFormat>();

	public DeckFormatProxy() {
		super(NAME);
	}

	public DeckFormat getDeckFormatByName(String deckName) {
		for (DeckFormat deckFormat : deckFormats) {
			if (deckFormat.getName().equals(deckName)) {
				return deckFormat;
			}
		}
		return null;
	}

	public List<DeckFormat> getDeckFormats() {
		return deckFormats;
	}

	public void loadDeckFormats() throws IOException, URISyntaxException {
		deckFormats.clear();
		DeckCatalogue.loadDeckFormats();
		deckFormats.addAll(DeckCatalogue.getDeckFormats());
	}
}
