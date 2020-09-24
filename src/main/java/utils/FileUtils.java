package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileUtils {
    private static final String delim = ",";

    private FileUtils() { }

    public static void writeContentToFile(String fileName, String content) {
        if (createFile(fileName)) {
            writeToFile(fileName, content);
        }
    }

    public static void writeContentToFile(String fileName, List<String> content) {
        if (createFile(fileName)) {
            writeToFile(fileName, String.join(delim, content));
        }
    }

    public static List<String> readCsvToList(String lunchOptionsUri) {

        return Arrays.stream(FileUtils.readFile(lunchOptionsUri).split(delim)).collect(Collectors.toList());
    }

    private static boolean createFile(String fileName) {
        File file = new File(fileName);
        boolean success = true;

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    private static boolean writeToFile(String fileName, String content) {
        boolean success = true;
        try {
            java.io.FileWriter myWriter = new java.io.FileWriter(fileName);
            myWriter.write(content);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            success = false;
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return success;
    }

    public static boolean doesFileExist(String fileName) {
        File f = new File(fileName);
        return f.exists() && !f.isDirectory();
    }

    public static String readFile(String fileName) {
        String data = "";
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return data;
    }
}
