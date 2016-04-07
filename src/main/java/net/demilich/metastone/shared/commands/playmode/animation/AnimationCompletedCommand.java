package net.demilich.metastone.shared.commands.playmode.animation;

import net.demilich.metastone.gui.playmode.animation.AnimationProxy;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.metastone.shared.GameNotification;

public class AnimationCompletedCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		AnimationProxy animationProxy = (AnimationProxy) getFacade().retrieveProxy(AnimationProxy.NAME);
		animationProxy.animationCompleted();
	}

}
