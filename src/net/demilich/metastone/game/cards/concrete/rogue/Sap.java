package net.demilich.metastone.game.cards.concrete.rogue;

import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.SpellCard;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.ReturnMinionToHandSpell;
import net.demilich.metastone.game.targeting.TargetSelection;

public class Sap extends SpellCard {

	public Sap() {
		super("Sap", Rarity.FREE, HeroClass.ROGUE, 2);
		setDescription("Return an enemy minion to your opponent's hand.");
		setSpell(ReturnMinionToHandSpell.create());
		setTargetRequirement(TargetSelection.ENEMY_MINIONS);
	}

	@Override
	public int getTypeId() {
		return 302;
	}
}