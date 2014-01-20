package net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.rogue;

import net.pferdimanzug.hearthstone.analyzer.game.cards.Rarity;
import net.pferdimanzug.hearthstone.analyzer.game.cards.SpellCard;
import net.pferdimanzug.hearthstone.analyzer.game.entities.heroes.HeroClass;
import net.pferdimanzug.hearthstone.analyzer.game.spells.ReturnMinionToHandSpell;
import net.pferdimanzug.hearthstone.analyzer.game.targeting.TargetSelection;

public class Sap extends SpellCard {

	public Sap() {
		super("Sap", Rarity.FREE, HeroClass.ROGUE, 2);
		setSpell(new ReturnMinionToHandSpell());
		setTargetRequirement(TargetSelection.ENEMY_MINIONS);
	}

}