package com.modernmt.text.profanity;

import com.modernmt.text.profanity.dictionary.Dictionary;
import com.modernmt.text.profanity.dictionary.Profanity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfanityFilter {

    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList(
            "ar", "az", "bg", "bs", "ca", "cs", "da", "de", "el", "en", "es", "et", "fi", "fr", "ga", "he", "hi", "hr",
            "hu", "hy", "id", "is", "it", "ja", "ka", "ko", "lt", "lv", "mk", "ms", "mt", "no", "nl", "pl", "pt", "ro",
            "ru", "sk", "sl", "sq", "sr", "sv", "sw", "th", "tl", "tr", "uk", "vi", "xh", "zh", "zu");


    private static final String DEFAULT_LANGUAGE = "en";

    private static Dictionary loadDictionary(String language) {
        String resource = "dictionary." + language;

        InputStream stream = ProfanityFilter.class.getResourceAsStream(resource);
        if (stream == null) throw new RuntimeException("Internal resource not found: " + resource);

        try {
            return Dictionary.read(language, stream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load internal resource: " + resource);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private final Map<String, Dictionary.Matcher> matchers;

    public ProfanityFilter() {
        this(0.0f);
    }

    public ProfanityFilter(float threshold) {
        matchers = new HashMap<>(SUPPORTED_LANGUAGES.size());
        for (String language : SUPPORTED_LANGUAGES) {
            Dictionary dictionary = loadDictionary(language);
            matchers.put(language, dictionary.matcher(threshold));
        }
    }

    public boolean test(String text){
        return test(DEFAULT_LANGUAGE, text);
    }

    public boolean test(String language, String text) {
        language = languageOf(language);

        Dictionary.Matcher matcher = matchers.get(language);
        return matcher != null && matcher.matches(text);
    }

    public Profanity find(String language, String text) {
        language = languageOf(language);

        Dictionary.Matcher matcher = matchers.get(language);
        return matcher != null ? matcher.find(text) : null;
    }

    private static String languageOf(String value) {
        int idx = value.indexOf('-');
        if (idx >= 0)
            value = value.substring(0, idx);

        if ("nn".equals(value) || "nb".equals(value))
            value = "no";

        return value;
    }

}
