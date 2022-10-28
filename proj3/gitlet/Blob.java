package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class creates a container for File contents that should persist,
 * which can be referenced from the Commit class by its _ID.
 * @author Kyle Casbon
 */
public class Blob implements Serializable {

    /**
     * The constructor writes the contents of a File to the .gitlet/blobs
     * directory and contains a reference to that File so that it can
     * be restored.
     * @param file the file that the client wishes to store
     */
    @SuppressWarnings("unchecked")
    Blob(File file) {
        _file = file;
        _contents = Utils.readContentsAsString(file);
        writeBlob(file);
        _id = createID();
        renamePath();
        ArrayList<Blob> addStage;
        ArrayList<String> remStag;
        remStag =
                Utils.readObject(Repository.getRemovalPath(), ArrayList.class);
        if (remStag.contains(this)) {
            remStag.remove(this);
        }
        addStage =
                Utils.readObject(Repository.getAdditionPath(), ArrayList.class);
        addStage.add(this);
        Utils.writeObject(Repository.getAdditionPath(), addStage);
    }

    String createID() {
        File temp = Utils.join(Repository.getBlobsFolder(), "temp");
        try {
            temp.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        Utils.writeObject(temp, this);
        return Utils.sha1(Utils.readContentsAsString(temp));
    }

    static void writeBlob(File file) {
        try {
            BLOB_PATH.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        Utils.writeObject(BLOB_PATH, file);
    }

    static void renamePath() {
        BLOB_PATH.renameTo(Utils.join(Repository.getBlobsFolder(), getID()));
    }

    static String getID() {
        return _id;
    }

    File getFile() {
        return this._file;
    }

    String getContents() {
        return _contents;
    }


    /**
     * The filepath for a Blob object.
     */
    private static final File BLOB_PATH =
            Utils.join(Repository.getBlobsFolder(), "temp");

    /**
     * The SHA-1 ID of the Blob.
     */
    private static String _id = null;

    /**
     * The file that the client wishes to store.
     */
    private File _file;

    /**
     * The contents of the file to be stored.
     */
    private String _contents;
}
