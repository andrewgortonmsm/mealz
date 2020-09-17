import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.base.Strings;

public class OptionFetcher {
    private static final String DINNER_OPTIONS_FILE = "dinners.csv";
    private static final String LUNCH_OPTIONS_FILE = "lunches.csv";

    private OptionFetcher() { }

    static Map<String, List<String>> getAllMealOptions() {
        Map<String, List<String>> mealOptions;

        if (FileUtils.doesFileExist(LUNCH_OPTIONS_FILE) && FileUtils.doesFileExist(DINNER_OPTIONS_FILE)) {
            mealOptions = getOptionsFromFile();
        } else {
            mealOptions = getOptionsFromGoogleSheet();
        }

        return mealOptions;
    }

    private static Map<String, List<String>> getOptionsFromFile() {
        Map<String, List<String>> mealOptions = new HashMap<>();
        mealOptions.put(Constants.LUNCH_OPTIONS_KEY, Arrays.stream(FileUtils.readFile(LUNCH_OPTIONS_FILE).split(",")).collect(Collectors.toList()));
        mealOptions.put(Constants.DINNER_OPTIONS_KEY, Arrays.stream(FileUtils.readFile(DINNER_OPTIONS_FILE).split(",")).collect(Collectors.toList()));
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

                FileUtils.writeContentToFile(LUNCH_OPTIONS_FILE, String.join(",", mealOptions.get(Constants.LUNCH_OPTIONS_KEY)));
                FileUtils.writeContentToFile(DINNER_OPTIONS_FILE, String.join(",", mealOptions.get(Constants.DINNER_OPTIONS_KEY)));
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
