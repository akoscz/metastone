package net.demilich.metastone.game.decks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.demilich.metastone.BuildConfig;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardSet;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.utils.ResourceInputStream;
import net.demilich.metastone.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DeckCatalogue {
    private static Logger logger = LoggerFactory.getLogger(DeckCatalogue.class);
    public static final String DECK_FORMATS_FOLDER = "formats";
    public static final String DECKS_FOLDER = "decks";

    private static final String DECKS_COPIED_PROPERTY = "decks.copied";
    private static final Collection<DeckFormat> deckFormats = new ArrayList<DeckFormat>();
    private static final Collection<Deck> deckList = new ArrayList<>();

    public static void loadDeckFormats() throws IOException, URISyntaxException {
        deckFormats.clear();
        // load the deck formats from the resources in cards.jar file on the classpath
        Collection<ResourceInputStream> inputStreams = ResourceLoader.getInstance().loadJsonInputStreams(DECK_FORMATS_FOLDER, false);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for (ResourceInputStream resourceInputStream : inputStreams) {
            Reader reader = new InputStreamReader(resourceInputStream.inputStream);
            HashMap<String, Object> map = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>() {}.getType());

            if (!map.containsKey("sets")) {
                logger.error("Deck {} does not specify a value for 'sets' and is therefore not valid", resourceInputStream.fileName);
                continue;
            }

            String deckName = (String) map.get("name");
            DeckFormat deckFormat = null;
            // this one is a meta deck; we need to parse those after all other
            // decks are done
            deckFormat = parseStandardDeckFormat(map);
            deckFormat.setName(deckName);
            deckFormat.setFilename(resourceInputStream.fileName);
            deckFormats.add(deckFormat);
        }
    }

    private static DeckFormat parseStandardDeckFormat(Map<String, Object> map) {
        DeckFormat deckFormat = new DeckFormat();
        @SuppressWarnings("unchecked")
        List<String> setIds = (List<String>) map.get("sets");
        for (String setId : setIds) {
            for (CardSet set : CardSet.values()) {
                if (set.toString().equalsIgnoreCase(setId)) {
                    deckFormat.addSet(set);
                }
            }
        }
        return deckFormat;
    }

    public static void loadDecks() throws IOException, URISyntaxException {
        // load decks from ~/metastone/decks on the filesystem
        if (new File((BuildConfig.USER_HOME_METASTONE + File.separator + DECKS_FOLDER)).exists()) {
            loadStandardDecks(ResourceLoader.getInstance().loadJsonInputStreams(BuildConfig.USER_HOME_METASTONE + File.separator + DECKS_FOLDER, true),
                    new GsonBuilder().setPrettyPrinting().create());

            loadMetaDecks(ResourceLoader.getInstance().loadJsonInputStreams(BuildConfig.USER_HOME_METASTONE + File.separator + DECKS_FOLDER, true),
                    new GsonBuilder().setPrettyPrinting().create());
        }
    }

    private static void loadMetaDecks(Collection<ResourceInputStream> inputStreams, Gson gson) throws IOException {
        for (ResourceInputStream resourceInputStream : inputStreams) {
            Reader reader = new InputStreamReader(resourceInputStream.inputStream);
            HashMap<String, Object> map = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>() {}.getType());
            if (!map.containsKey("heroClass")) {
                logger.error("Deck {} does not specify a value for 'heroClass' and is therefor not valid", resourceInputStream.fileName);
                continue;
            }
            String deckName = (String) map.get("name");
            Deck deck = null;
            if (!map.containsKey("decks")) {
                continue;
            } else {
                deck = parseMetaDeck(map);
            }
            deck.setName(deckName);
            deck.setFilename(resourceInputStream.fileName);
            deckList.add(deck);
        }
    }

    private static void loadStandardDecks(Collection<ResourceInputStream> inputStreams, Gson gson) throws FileNotFoundException {
        for (ResourceInputStream resourceInputStream : inputStreams) {

            Reader reader = new InputStreamReader(resourceInputStream.inputStream);
            HashMap<String, Object> map = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>() {}.getType());
            if (!map.containsKey("heroClass")) {
                logger.error("Deck {} does not speficy a value for 'heroClass' and is therefor not valid", resourceInputStream.fileName);
                continue;
            }
            HeroClass heroClass = HeroClass.valueOf((String) map.get("heroClass"));
            String deckName = (String) map.get("name");
            Deck deck = null;
            // this one is a meta deck; we need to parse those after all other
            // decks are done
            if (map.containsKey("decks")) {
                continue;
            } else {
                deck = parseStandardDeck(heroClass, map);
            }
            deck.setName(deckName);
            deck.setFilename(resourceInputStream.fileName);
            deckList.add(deck);
        }
    }

    public static Deck getDeckByName(String deckName) {
        for (Deck deck : deckList) {
            if (deck.getName().equals(deckName)) {
                return deck;
            }
        }
        return null;
    }

    public static List<Deck> getAllDecks() {
        return new ArrayList<>(deckList);
    }

    public static List<DeckFormat> getDeckFormats() {
        return new ArrayList<>(deckFormats);
    }

    public boolean nameAvailable(Deck deck) {
        for (Deck existingDeck : deckList) {
            if (existingDeck != deck && existingDeck.getName().equals(deck.getName())) {
                return false;
            }
        }
        return true;
    }

    private static Deck parseMetaDeck(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<String> referencedDecks = (List<String>) map.get("decks");
        List<Deck> decksInMetaDeck = new ArrayList<>();
        for (String deckName : referencedDecks) {
            Deck deck = getDeckByName(deckName);
            if (deck == null) {
                logger.error("Metadeck {} contains invalid reference to deck {}", map.get("name"), deckName);
                continue;
            }
            decksInMetaDeck.add(deck);
        }
        return new MetaDeck(decksInMetaDeck);
    }

    private static Deck parseStandardDeck(HeroClass heroClass, Map<String, Object> map) {
        Deck deck = new Deck(heroClass);
        @SuppressWarnings("unchecked")
        List<String> cardIds = (List<String>) map.get("cards");
        for (String cardId : cardIds) {
            Card card = CardCatalogue.getCardById(cardId);
            deck.getCards().add(card);
        }
        return deck;
    }

    public static void copyDecksFromJar() throws IOException, URISyntaxException {
        Properties prop = new Properties();
        InputStream input = null;
        FileOutputStream output = null;
        String propertiesFilePath = BuildConfig.USER_HOME_METASTONE + File.separator + "metastone.properties";
        try {
            File propertiesFile = new File(propertiesFilePath);
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }

            input = new FileInputStream(propertiesFile);
            // load a properties file
            prop.load(input);

            // if we have not copied decks to the USER_HOME_METASTONE decks folder, then do so now
            if (!Boolean.parseBoolean(prop.getProperty(DECKS_COPIED_PROPERTY))) {
                ResourceLoader.copyFromResources(DECKS_FOLDER, BuildConfig.USER_HOME_METASTONE + File.separator + DECKS_FOLDER);

                output = new FileOutputStream(propertiesFile);
                // set a property to indicate that we have copied decks
                prop.setProperty(DECKS_COPIED_PROPERTY, Boolean.TRUE.toString());
                // write properties file
                prop.store(output, null);
            }

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
