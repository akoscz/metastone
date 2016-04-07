package net.demilich.metastone.game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CardCollection implements Iterable<Card>, Cloneable {

	private List<Card> cards = new ArrayList<Card>();

	public CardCollection() {

	}

	public void add(Card card) {
		cards.add(card);
	}

	public void addAll(CardCollection cardCollection) {
		for (Card card : cardCollection) {
			cards.add(card.clone());
		}
	}
	
	public void addRandomly(Card card) {
		int index = ThreadLocalRandom.current().nextInt(cards.size() + 1);
		cards.add(index, card);
	}

	public CardCollection clone() {
		CardCollection clone = new CardCollection();
		for (Card card : cards) {
			clone.add(card.clone());
		}

		return clone;
	}

	public boolean contains(Card card) {
		return cards.contains(card);
	}
	
	public boolean containsCard(Card card) {
		if (card == null) {
			return false;
		}
		for (Card other : cards) {
			if (other.getCardId().equals(card.getCardId())) {
				return true;
			}
		}
		return false;
	}

	public Card get(int index) {
		return cards.get(index);
	}

	public int getCount() {
		return cards.size();
	}

	public Card getRandom() {
		if (cards.isEmpty()) {
			return null;
		}
		return cards.get(ThreadLocalRandom.current().nextInt(cards.size()));
	}

	public Card getRandomOfType(CardType cardType) {
		List<Card> relevantCards = new ArrayList<>();
		for (Card card : cards) {
			if (card.getCardType() == cardType) {
				relevantCards.add(card);
			}
		}
		if (relevantCards.isEmpty()) {
			return null;
		}
		return relevantCards.get(ThreadLocalRandom.current().nextInt(relevantCards.size()));
	}

	public boolean hasCardOfType(CardType cardType) {
		for (Card card : cards) {
			if (card.getCardType() == cardType) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	public Card peekFirst() {
		return cards.get(0);
	}

	public boolean remove(Card card) {
		return cards.remove(card);
	}

	public void removeAll() {
		cards.clear();
	}

	public Card removeFirst() {
		return cards.remove(0);
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public void sortByManaCost() {
		Collections.sort(cards, (card1, card2) -> Integer.valueOf(card1.getBaseManaCost()).compareTo(card2.getBaseManaCost()));
	}

	public void sortByName() {
		Collections.sort(cards, (card1, card2) -> card1.getName().compareTo(card2.getName()));
	}

	public List<Card> toList() {
		return new ArrayList<>(cards);
	}

}
