package net.pferdimanzug.hearthstone.analyzer.gui.main;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import net.pferdimanzug.hearthstone.analyzer.GameNotification;
import net.pferdimanzug.hearthstone.analyzer.gui.deckbuilder.DeckBuilderMediator;
import net.pferdimanzug.hearthstone.analyzer.gui.mainmenu.MainMenuMediator;
import net.pferdimanzug.hearthstone.analyzer.gui.playmode.PlayModeMediator;
import net.pferdimanzug.hearthstone.analyzer.gui.playmode.config.PlayModeConfigMediator;
import de.pferdimanzug.nittygrittymvc.Mediator;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class ApplicationMediator extends Mediator<GameNotification> {

	public static final String NAME = "ApplicationMediator";

	private Pane root;

	public ApplicationMediator() {
		super(NAME);
	}

	@Override
	public void handleNotification(final INotification<GameNotification> notification) {
		switch (notification.getId()) {
		case CANVAS_CREATED:
			root = (Pane) notification.getBody();
			break;
		case SHOW_VIEW:
			Node view = (Node) notification.getBody();
			root.getChildren().clear();
			root.getChildren().add(view);
			break;
		case MAIN_MENU:
			removeOtherViews();
			getFacade().registerMediator(new MainMenuMediator());
			break;
		default:
			break;
		}
	}
	
	private void removeOtherViews() {
		getFacade().removeMediator(PlayModeMediator.NAME);
		getFacade().removeMediator(PlayModeConfigMediator.NAME);
		getFacade().removeMediator(DeckBuilderMediator.NAME);
	}

	@Override
	public List<GameNotification> listNotificationInterests() {
		List<GameNotification> notificationInterests = new ArrayList<GameNotification>();
		notificationInterests.add(GameNotification.CANVAS_CREATED);
		notificationInterests.add(GameNotification.SHOW_VIEW);
		notificationInterests.add(GameNotification.MAIN_MENU);
		return notificationInterests;
	}

}