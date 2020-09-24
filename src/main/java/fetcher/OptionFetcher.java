package fetcher;

import static utils.FileUtils.readCsvToList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.base.Strings;

import utils.Constants;
import utils.FileUtils;

public class OptionFetcher {
    private static final String DINNER_OPTIONS_FILE = "dinners.csv";
    private static final String LUNCH_OPTIONS_FILE = "lunches.csv";
    private static final String OPTIONS_DIRECTORY = "options";

    private static final String LUNCH_OPTIONS_URI = OPTIONS_DIRECTORY + "/" + LUNCH_OPTIONS_FILE;
    private static final String DINNER_OPTIONS_URI = OPTIONS_DIRECTORY + "/" + DINNER_OPTIONS_FILE;

    private OptionFetcher() { }

    public static Map<String, List<String>> getAllMealOptions() {
        Map<String, List<String>> mealOptions;

        if (FileUtils.doesFileExist(LUNCH_OPTIONS_URI) && FileUtils.doesFileExist(DINNER_OPTIONS_URI)) {
            mealOptions = getOptionsFromFile();
        } else {
            mealOptions = getOptionsFromGoogleSheet();
        }

        return mealOptions;
    }

    private static Map<String, List<String>> getOptionsFromFile() {
        Map<String, List<String>> mealOptions = new HashMap<>();
        mealOptions.put(Constants.LUNCH_OPTIONS_KEY, readCsvToList(LUNCH_OPTIONS_URI));
        mealOptions.put(Constants.DINNER_OPTIONS_KEY, readCsvToList(DINNER_OPTIONS_URI));
        return mealOptions;
    }

    private static Map<String, List<String>> getOptionsFromGoogleSheet() {
        Map<String, List<String>> mealOptions = new HashMap<>();

        try {
            ValueRange response = GoogleSheetFetcher.fetchValuesFromGoogleSheet();
            if (response.getValues() == null || response.getValues().isEmpty()) {
                System.out.println("No data found.");
            } else {
                mealOptions.put(Constants.LUNCH_OPTIONS_KEY, toOptionList(response, 0));
                mealOptions.put(Constants.DINNER_OPTIONS_KEY, toOptionList(response, 1));

                FileUtils.writeContentToFile(LUNCH_OPTIONS_URI, mealOptions.get(Constants.LUNCH_OPTIONS_KEY));
                FileUtils.writeContentToFile(DINNER_OPTIONS_URI, mealOptions.get(Constants.DINNER_OPTIONS_KEY));
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return mealOptions;
    }

    private static List<String> toOptionList(ValueRange values, int position) {
        return values.getValues().stream().skip(1)
            .map(valuePair -> valuePair.get(position).toString())
            .filter(option -> !Strings.isNullOrEmpty(option))
            .collect(Collectors.toList());
    }
}
