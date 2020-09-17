import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileUtils {

    private FileUtils() { }

    static boolean writeContentToFile(String fileName, String content) {
        return createFile(fileName) && writeToFile(fileName, content);
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

    static boolean doesFileExist(String fileName) {
        File f = new File(fileName);
        return f.exists() && !f.isDirectory();
    }

    static String readFile(String fileName) {
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
