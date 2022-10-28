package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.File;

public class Commit implements Serializable {

    Commit(Date date, String message, ArrayList<Blob> add,
           ArrayList<String> rem) {
        _date = date;
        _message = message;
        _parent = findParent();

        File commBlobs = Utils.join(Repository.getBlobStore(), "temp");
        if (!add.isEmpty()) {
            commBlobs.mkdir();
        }
        for (int i = 0; i
                < add.toArray().length; i += 1) {
            Blob blob = add.get(i);
            File blobStorage =
                    Utils.join(commBlobs, blob.getFile().toString());
            try {
                blobStorage.createNewFile();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
            Utils.writeObject(blobStorage, blob);
        }
        add.clear();
        rem.clear();
        Utils.writeObject(Repository.getAdditionPath(), add);
        Utils.writeObject(Repository.getRemovalPath(), rem);
        _ID = createID();
        renameComm();
        File commStore = Utils.join(Repository.getAllComm(), getID());
        try {
            commStore.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        Utils.writeObject(commStore, this);
        if (getParent() == null) {
            File branchHead = new File(".gitlet/branches/master/HEAD");
            try {
                branchHead.createNewFile();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
            Utils.writeContents(branchHead, getID());
        }
        updateHead();
    }

    void updateHead() {
        Utils.writeContents(Repository.getHead(), getID());
        File currBranchHead = new File(".gitlet/branches/"
                + Utils.readContentsAsString(Repository.getCurrBranch())
                + "/HEAD");
        Utils.writeContents(currBranchHead, getID());
    }

    void renameComm() {
        File branch = Utils.join(Repository.getBranchesFolder(),
                Utils.readContentsAsString(Repository.getCurrBranch()));
        File newPath = Utils.join(branch, getID());
        Utils.join(branch, "temp").renameTo(newPath);
        File commBlobs = Utils.join(Repository.getBlobStore(), "temp");
        commBlobs.renameTo(Utils.join(Repository.getBlobStore(), getID()));
    }

    String createID() {
        File branch = Utils.join(Repository.getBranchesFolder(),
                Utils.readContentsAsString(Repository.getCurrBranch()));
        File temp = Utils.join(branch, "temp");
        try {
            temp.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        Utils.writeObject(temp, this);
        return Utils.sha1(Utils.readContentsAsString(temp));
    }

    String findParent() {
        if (getMessage() == "initial commit") {
            return null;
        }
        return Utils.readContentsAsString(Repository.getHead());
    }

    void checkoutBasic(String arg2, Commit currComm) {
        String blobContents = null;

        List<String> allBlobs =
                Utils.plainFilenamesIn(Repository.getBlobsFolder());
        ArrayList<String> blobArr = new ArrayList<>();
        blobArr.addAll(allBlobs);

        for (String id : blobArr) {
            File blobPath = Utils.join(Repository.getBlobsFolder(), id);
            Blob blob = Utils.readObject(blobPath, Blob.class);
            if (blob.getFile().toString().equals(arg2)) {
                blobContents = blob.getContents();
            }
        }

        File overFile = new File(".gitlet/blob_storage/"
                + currComm.createID() + "/" + arg2);
        if (overFile.exists()) {
            File oldFile = Utils.join(Main.CWD.getPath(), arg2);
            if (oldFile.exists()) {
                Utils.restrictedDelete(oldFile);
            }
            File newPath = Utils.join(Main.CWD.getPath(), arg2);

            Utils.writeContents(newPath, blobContents);
        } else {
            System.out.println("File does not exist in that commit.");

        }
    }

    void checkoutOld(String file, String commID) {
        String blobContents = null;

        File currCommStore = Utils.join(Repository.getBlobStore(), commID);
        List<String> allBlobs = Utils.plainFilenamesIn(currCommStore);
        ArrayList<String> blobArr = new ArrayList<>();
        blobArr.addAll(allBlobs);

        for (String id : blobArr) {
            File blobPath = Utils.join(currCommStore, id);
            Blob blob = Utils.readObject(blobPath, Blob.class);
            if (id.equals(file)) {
                blobContents = blob.getContents();
            }
        }

        File overFile = new File(".gitlet/blob_storage/"
                + commID + "/" + file);
        if (overFile.exists()) {
            File oldFile = Utils.join(Main.CWD.getPath(), file);
            if (oldFile.exists()) {
                Utils.restrictedDelete(oldFile);
            }
            File newPath = Utils.join(Main.CWD.getPath(), file);

            Utils.writeContents(newPath, blobContents);
        } else {
            System.out.println("File does not exist in that commit.");

        }
    }

    String commitToString() {
        return String.format("===") + "\n"
                + "commit " + createID() + "\n"
                + String.format("Date: ") + getDate() + "\n"
                + getMessage() + "\n";
    }

    private String getDate() {
        SimpleDateFormat format =
                new SimpleDateFormat("EEE MMM d hh:mm:ss YYYY Z");
        return format.format(_date);
    }

    String getMessage() {
        return _message;
    }

    String getID() {
        return _ID;
    }

    String getParent() {
        return _parent;
    }

    String getParent2() {
        return _parent2;
    }

    /**
     * This date object represents the exact time/date
     * of the creation of this commit object.
     */
    private final Date _date;

    /**
     * The message assigned to the commit object by
     * the client.
     */
    private final String _message;

    /**
     * The hashed SHA-1 ID of the commit object.
     */
    private final String _ID;

    /**
     * The parent commit of the given commit object.
     * This is stored, not as a pointer to an
     * object, but as the SHA-1 ID that can be
     * used to reference the Commit object.
     */
    private String _parent;

    /**
     * Parent2 will always be null except in the
     * case of a merge.
     */
    private String _parent2 = null;
}
