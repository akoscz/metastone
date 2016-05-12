package net.demilich.metastone.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.decks.DeckCatalogue;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnPageChange;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HeroSelectionActivity extends Activity {

    private static final String TAG = "HeroSelectionActivity";

    @BindView(R.id.player1_panel) ViewPager mPlayer1ViewPager;
    @BindView(R.id.player2_panel) ViewPager mPlayer2ViewPager;
    @BindView(R.id.deck_format) Spinner mDeckFormatSpiner;
    @BindView(R.id.play_button) ImageButton mPlayButton;
    private HeroSelectionPagerAdapter mPlayer1Adapter;
    private HeroSelectionPagerAdapter mPlayer2Adapter;
    private List<DeckFormat> mDeckFormats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_selection);
        ButterKnife.bind(this);

        List<Deck> deckList = DeckCatalogue.getAllDecks();
        mDeckFormats = DeckCatalogue.getDeckFormats();

        mPlayer1Adapter = new HeroSelectionPagerAdapter(this, mDeckFormats.get(0), deckList, PlayerType.HUMAN);
        mPlayer2Adapter = new HeroSelectionPagerAdapter(this, mDeckFormats.get(0), deckList, PlayerType.OPPONENT);
        mPlayer1ViewPager.setAdapter(mPlayer1Adapter);
        mPlayer2ViewPager.setAdapter(mPlayer2Adapter);

        DeckFormatsAdapter deckFormatsAdapter = new DeckFormatsAdapter(this, R.layout.metastone_spinner_item, mDeckFormats);
        deckFormatsAdapter.setDropDownViewResource(R.layout.metastone_spinner_dropdown_item);

        mDeckFormatSpiner.setAdapter(deckFormatsAdapter);
    }

    @OnPageChange(R.id.player1_panel) void onPlayer1PageChanged(int position){

    }

    @OnPageChange(R.id.player1_panel) void onPlayer2PageChanged(int position) {

    }

    @OnItemSelected(R.id.deck_format) void onDeckFormatItemSelected(int position) {
        DeckFormat format = mDeckFormats.get(position);
        mPlayer1Adapter.filterDecks(format, mPlayer1ViewPager.getCurrentItem());
        mPlayer2Adapter.filterDecks(format, mPlayer2ViewPager.getCurrentItem());

        if (format.getName().toLowerCase().equals("wild")) {
            mPlayButton.setBackgroundResource(R.drawable.play_wild);
        } else if (format.getName().toLowerCase().equals("standard")) {
            mPlayButton.setBackgroundResource(R.drawable.play_standard);
        } else {
            mPlayButton.setBackgroundResource(R.drawable.play);
        }
    }

    @OnClick(R.id.play_button) void onPlayButtonClicked() {
        Log.d(TAG, mPlayer1Adapter.getPlayerConfig().toString());
        Log.d(TAG, mPlayer2Adapter.getPlayerConfig().toString());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class DeckFormatsAdapter extends ArrayAdapter<DeckFormat> {

        public DeckFormatsAdapter(Context context, int resource, Collection<DeckFormat> objects) {
            super(context, resource, (List<DeckFormat>) objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            v.setTag(getItem(position));
            return v;
        }
    }
}
