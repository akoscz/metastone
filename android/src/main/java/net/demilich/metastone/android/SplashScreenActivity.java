package net.demilich.metastone.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.decks.DeckCatalogue;

import java.io.IOException;
import java.net.URISyntaxException;

import butterknife.BindView;

public class SplashScreenActivity extends Activity {

    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new LoadDataAsyncTask().execute();
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CardCatalogue.loadCards();
                DeckCatalogue.loadDeckFormats();
                DeckCatalogue.loadDecks();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
