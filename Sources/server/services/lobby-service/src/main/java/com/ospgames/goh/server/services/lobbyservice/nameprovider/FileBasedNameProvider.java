package com.ospgames.goh.server.services.lobbyservice.nameprovider;

import com.ospgames.goh.server.services.lobbyservice.INameProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Uses the lines of a file as names.
 */
public class FileBasedNameProvider implements INameProvider {

    public static Log sLog = LogFactory.getLog(FileBasedNameProvider.class);
    public final Set<String> mNames;

    public FileBasedNameProvider(String file) throws FileNotFoundException {
        mNames = Collections.unmodifiableSet(readFile(file));
    }

    public Iterator<String> iterator() {
        return mNames.iterator();
    }

    static Set<String> readFile(String file) throws FileNotFoundException {

        InputStream fileIn = ClassLoader.getSystemResourceAsStream(file);
        if (fileIn == null) throw new FileNotFoundException(file);

        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(fileIn));

        Set<String> names = new HashSet<String>();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.length()>0) {
                    names.add(line);
                }
            }
        } catch (IOException e) {
            sLog.equals(e);
        }
        return names;
    }
}
