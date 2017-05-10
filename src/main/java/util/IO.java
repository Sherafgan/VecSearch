package util;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/13/17
 */
public class IO {
    private final FileReader fileReader;
    private final BufferedReader bufferedReader;

    private String id;
    private String vector;

    public IO(String fileName) throws FileNotFoundException {
        this.fileReader = new FileReader(fileName);
        this.bufferedReader = new BufferedReader(fileReader);
    }

    public Pair<String, String> getNextPair() throws IOException {
        Pair<String, String> idAndVector;
        if (this.id != null && this.vector != null) {
            idAndVector = new Pair<>(id, vector);
            return idAndVector;
        } else {
            return null;
        }
    }

    public boolean hasNextPair() {
        Pair<String, String> idAndVector;
        try {
            this.id = bufferedReader.readLine();
            this.vector = bufferedReader.readLine();
            if (this.id != null && this.vector != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}
