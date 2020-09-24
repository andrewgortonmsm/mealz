import static java.lang.String.format;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import fetcher.OptionFetcher;
import utils.Constants;
import utils.FileUtils;

public class Meals {

    private static final String LUNCH_AND_DINNER_RESULTS_FILE = "lunchesAndDinners.csv";

    /* TODO:
    *   - Add Args handling.
    *   - Add ability to reroll specific day / meal.
    * */
    public static void main(String... args) {
        Map<String, List<String>> allOptions = OptionFetcher.getAllMealOptions();
        List<String> lunchOptions = allOptions.get(Constants.LUNCH_OPTIONS_KEY);
        List<String> dinnerOptions = allOptions.get(Constants.DINNER_OPTIONS_KEY);

        Arrays.stream(args).forEach(System.out::println);

        generateMealsForWorkingWeek(lunchOptions, dinnerOptions);
    }

    private static void generateMealsForWorkingWeek(List<String> lunchOptions, List<String> dinnerOptions) {
        Supplier<Stream<Integer>> integers = () -> Stream.of(0, 1, 2, 3, 4);

        List<String> lunches = integers.get().map(dayOfWeek -> getMeal(lunchOptions)).collect(Collectors.toList());
        List<String> dinners = integers.get().map(dayOfWeek -> getMeal(dinnerOptions)).collect(Collectors.toList());

        String fullResultUri = Constants.RESULT_DIRECTORY + "/" + LUNCH_AND_DINNER_RESULTS_FILE;
        FileUtils.writeContentToFile(fullResultUri, resultToCsv(lunches, dinners));
    }

    private static String resultToCsv(List<String> lunches, List<String> dinners) {
        return String.join("\n", Lists.newArrayList(
            format("Monday,%s,%s", lunches.get(0), dinners.get(0)),
            format("Tuesday,%s,%s", lunches.get(1), dinners.get(1)),
            format("Wednesday,%s,%s", lunches.get(2), dinners.get(2)),
            format("Thursday,%s,%s", lunches.get(3), dinners.get(3)),
            format("Friday,%s,%s", lunches.get(4), dinners.get(4))
        ));
    }

    private static String getMeal(List<String> mealOptions) {
        Random random = new Random();
        int randomInteger = random.ints(0, mealOptions.size()).findFirst().getAsInt();
        String choice = mealOptions.get(randomInteger);
        mealOptions.remove(randomInteger);
        return choice;
    }
}
