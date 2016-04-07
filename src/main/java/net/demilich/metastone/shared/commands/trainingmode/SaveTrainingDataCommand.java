package net.demilich.metastone.shared.commands.trainingmode;

import net.demilich.metastone.gui.trainingmode.TrainingProxy;
import net.demilich.metastone.shared.trainingmode.TrainingData;
import net.demilich.nittygrittymvc.SimpleCommand;
import net.demilich.nittygrittymvc.interfaces.INotification;
import net.demilich.metastone.shared.GameNotification;

public class SaveTrainingDataCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		TrainingData trainingData = (TrainingData) notification.getBody();

		TrainingProxy trainingProxy = (TrainingProxy) getFacade().retrieveProxy(TrainingProxy.NAME);
		trainingProxy.saveTrainingData(trainingData);
		;
	}

}
