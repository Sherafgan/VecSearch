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

    public IO(String fileName) throws FileNotFoundException {
        this.fileReader = new FileReader(fileName);
        this.bufferedReader = new BufferedReader(fileReader);
    }

    public Pair<String, String> readNextPair() throws IOException {
        Pair<String, String> idAndVector;
        String id = bufferedReader.readLine();
        String vector = bufferedReader.readLine();
        if (id != null && vector != null) {
            idAndVector = new Pair<>(id, vector);
        } else {
            throw new IOException();
        }
        return idAndVector;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}
