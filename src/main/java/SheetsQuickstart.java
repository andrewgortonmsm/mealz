import static java.lang.String.format;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.base.Strings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "Mealz";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8999).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "11Ed5qvTalNjN6A9efpTCnjKDMijnoahayLg2D0d4jd8";
        final String range = "'Sheet2'!A:B";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            List<String> lunchOptions = toOptionList(response, 0);
            List<String> dinnerOptions = toOptionList(response, 1);

            Supplier<Stream<Integer>> integers = () -> Stream.of(0, 1, 2, 3, 4);
            List<String> lunches = integers.get().map(dayOfWeek -> getMeal(lunchOptions)).collect(Collectors.toList());
            List<String> dinners = integers.get().map(dayOfWeek -> getMeal(dinnerOptions)).collect(Collectors.toList());

            System.out.println("Day: Lunch \t Dinner");
            System.out.println(format("Monday: %s \t %s", lunches.get(0), dinners.get(0)));
            System.out.println(format("Tuesday: %s \t %s", lunches.get(1), dinners.get(1)));
            System.out.println(format("Wednesday: %s \t %s", lunches.get(2), dinners.get(2)));
            System.out.println(format("Thursday: %s \t %s", lunches.get(3), dinners.get(3)));
            System.out.println(format("Friday: %s \t %s", lunches.get(4), dinners.get(4)));
        }
    }

    private static String getMeal(List<String> mealOptions){
        Random random = new Random();
        int randomInteger = random.ints(0, mealOptions.size()).findFirst().getAsInt();
        String choice = mealOptions.get(randomInteger);
        mealOptions.remove(randomInteger);
        return choice;
    }

    private static List<String> toOptionList(ValueRange values, int position){
        return values.getValues().stream().skip(1)
            .map(valuePair -> valuePair.get(position).toString())
            .filter(option -> !Strings.isNullOrEmpty(option))
            .collect(Collectors.toList());
    }
}