package net.demilich.metastone.android;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.NoAggressionBehaviour;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.human.HumanBehaviour;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFactory;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.PlayerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeroSelectionPagerAdapter extends PagerAdapter {

    private final List<Deck> mDeckList;
    private final Context mContext;
    private final List<Behaviour> mBehaviourList = new ArrayList<>();
    private final Map<HeroClass, List<Deck>> mFilteredDeckMap = new HashMap<>();
    private final List<HeroData> mHeroesList = new ArrayList<>();
    private final Map<Integer, View> mPageViews = new HashMap();
    private final LayoutInflater mInflater;
    private final PlayerConfig mPlayerConfig;

    public HeroSelectionPagerAdapter(Context context, DeckFormat format, List<Deck> deckList, PlayerType playerType) {
        super();
        mContext = context;
        mDeckList = deckList;
        mInflater= LayoutInflater.from(context);

        HeroData heroData;
        // populate hero list and filter decks per hero
        for (Card card : CardCatalogue.getHeroes()) {
            heroData= new HeroData((HeroCard) card);
            mHeroesList.add(heroData);
            filterDecks(format, heroData.heroClass);
        }

        // populate behavior list
        if (playerType == PlayerType.HUMAN || playerType == PlayerType.OPPONENT || playerType == PlayerType.SANDBOX) {
            mBehaviourList.add(new HumanBehaviour());
        }
        mBehaviourList.add(new GameStateValueBehaviour());
        mBehaviourList.add(new PlayRandomBehaviour());
        mBehaviourList.add(new GreedyOptimizeMove(new WeightedHeuristic()));
        mBehaviourList.add(new NoAggressionBehaviour());

        // setup defaults
        mPlayerConfig = new PlayerConfig(mDeckList.get(0), mBehaviourList.get(0));
        mPlayerConfig.setName(mHeroesList.get(0).name);
    }

    public PlayerConfig getPlayerConfig() {
        return mPlayerConfig;
    }

    @Override
    public int getCount() {
        return mHeroesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPlayerConfig.setName(mHeroesList.get(position).name);
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {

        View view;
        ViewHolder holder;

        // instantiate the Hero Selection Panel if we dont have one cached already
        if (mPageViews.containsKey(position)) {
            view = mPageViews.get(position);
            holder = (ViewHolder) view.getTag();
        } else {
            view = mInflater.inflate(R.layout.hero_selection_panel, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        HeroData heroData = mHeroesList.get(position);
        holder.heroClass.setText(heroData.heroClass.toString());
        holder.heroPortrait.setImageResource(heroData.portraitResourceId);
        holder.heroName.setText(heroData.name);

        final ArrayAdapter<Behaviour> behaviorAdapter
                = new ArrayAdapter<>(mContext,
                    R.layout.metastone_spinner_item,
                    mBehaviourList.toArray(new Behaviour[mBehaviourList.size()]));
        behaviorAdapter.setDropDownViewResource(R.layout.metastone_spinner_dropdown_item);
        holder.behavior.setAdapter(behaviorAdapter);
        holder.behavior.setPopupBackgroundDrawable(null);
        holder.behavior.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPlayerConfig.setBehaviour(behaviorAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        final ArrayAdapter<Deck> deckAdapter
                = new ArrayAdapter<>(mContext,
                    R.layout.metastone_spinner_item,
                    mFilteredDeckMap.get(heroData.heroClass));
        deckAdapter.setDropDownViewResource(R.layout.metastone_spinner_dropdown_item);
        holder.deck.setAdapter(deckAdapter);
        holder.deck.setPopupBackgroundDrawable(null);
        holder.deck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPlayerConfig.setDeck(deckAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        holder.hideCards.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPlayerConfig.setHideCards(b);
            }
        });

        if (container != null) container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    public void filterDecks(DeckFormat deckFormat, int position) {
        filterDecks(deckFormat, mHeroesList.get(position).heroClass);
    }

    public void filterDecks(DeckFormat deckFormat, HeroClass heroClass) {
        List<Deck> deckList = new ArrayList<>();

        if (heroClass == HeroClass.DECK_COLLECTION) {
            for (Deck deck : mDeckList) {
                if (deck.getHeroClass() != HeroClass.DECK_COLLECTION) {
                    continue;
                }
                if (deckFormat != null && deckFormat.inSet(deck)) {
                    deckList.add(deck);
                }
            }
        } else {
            Deck randomDeck = DeckFactory.getRandomDeck(heroClass);
            deckList.add(randomDeck);
            for (Deck deck : mDeckList) {
                if (deck.getHeroClass() == HeroClass.DECK_COLLECTION) {
                    continue;
                }
                if (deck.getHeroClass() == heroClass || deck.getHeroClass() == HeroClass.ANY) {
                    if (deckFormat != null && deckFormat.inSet(deck)) {
                        deckList.add(deck);
                    }
                }
            }
        }

        mFilteredDeckMap.put(heroClass, deckList);
    }

    static final class ViewHolder {
        @BindView(R.id.hero_class) TextView heroClass;
        @BindView(R.id.hero_portrait) ImageView heroPortrait;
        @BindView(R.id.hero_name) TextView heroName;
        @BindView(R.id.behavior) Spinner behavior;
        @BindView(R.id.deck) Spinner deck;
        @BindView(R.id.hide_cards) CheckBox hideCards;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static final class HeroData {
        public final String name;
        public final HeroClass heroClass;
        public final int portraitResourceId;

        public HeroData(HeroCard heroCard) {
            heroClass = heroCard.getHeroClass();
            name = heroCard.getName();

            switch (heroClass) {
                case DRUID:
                    portraitResourceId = R.drawable.malfurion;
                    break;
                case HUNTER:
                    portraitResourceId = R.drawable.rexxar;
                    break;
                case MAGE:
                    portraitResourceId = R.drawable.jaina;
                    break;
                case PALADIN:
                    portraitResourceId = R.drawable.uther;
                    break;
                case PRIEST:
                    portraitResourceId = R.drawable.anduin;
                    break;
                case ROGUE:
                    portraitResourceId = R.drawable.valeera;
                    break;
                case SHAMAN:
                    portraitResourceId = R.drawable.thrall;
                    break;
                case WARLOCK:
                    portraitResourceId = R.drawable.guldan;
                    break;
                case WARRIOR:
                    portraitResourceId = R.drawable.garrosh;
                    break;
                case ANY:
                case DECK_COLLECTION:
                case OPPONENT:
                case BOSS:
                default:
                    throw new RuntimeException("Invalid HeroClass: " + heroClass);
            }
        }
    }
}
