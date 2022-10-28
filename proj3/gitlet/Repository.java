package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents an instantiated .gitlet repository. Only
 * one of these objects should exist in a given directory.
 * @author Kyle Casbon
 */
public class Repository {

    /**
     * The Repository.java constructor creates
     * the .gitlet and .gitlet/blobs directories
     * and instantiates the _ADDITIONSTAGE and
     * _REMOVALSTAGE variables.
     */
    Repository() {
        GITLET_FOLDER.mkdir();
        BLOBS_FOLDER.mkdir();
        BRANCHES_FOLDER.mkdir();
        blobStore.mkdir();
        allComm.mkdir();
        allBranch.mkdir();
        _additionStage = new ArrayList<>();
        _removalStage = new ArrayList<>();
        try {
            ADDITION_PATH.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        try {
            REMOVAL_PATH.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        try {
            head.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        try {
            currBranch.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        Utils.writeObject(ADDITION_PATH, _additionStage);
        Utils.writeObject(REMOVAL_PATH, _removalStage);
        Utils.writeContents(Repository.getCurrBranch(), "master");
    }

    static File getGitletFolder() {
        return GITLET_FOLDER;
    }

    static File getBlobsFolder() {
        return BLOBS_FOLDER;
    }

    static File getBranchesFolder() {
        return BRANCHES_FOLDER;
    }

    static File getAdditionPath() {
        return ADDITION_PATH;
    }

    static File getRemovalPath() {
        return REMOVAL_PATH;
    }

    static File getHead() {
        return head;
    }

    static File getCurrBranch() {
        return currBranch;
    }

    static File getBlobStore() {
        return blobStore;
    }

    static File getAllComm() {
        return allComm;
    }

    static File getAllBranch() {
        return  allBranch;
    }


    /**
     * The filepath to create the .gitlet hidden directory.
     */
    private static final File GITLET_FOLDER = new File(".gitlet");

    /**
     * The filepath to the .gitlet/blobs directory
     * which should contain "Blob Files".
     */
    private static final File BLOBS_FOLDER = new File(".gitlet/blobs");

    /**
     * The filepath to the ".gitlet/branches" folder.
     */
    private static final File BRANCHES_FOLDER = new File(".gitlet/branches");

    /**
     * The staging area for all added files before
     * they are committed. It holds the BLOB object
     * the file.
     */
    private static ArrayList<Blob> _additionStage;

    /**
     * The staging area for all removed files before
     * they are committed. It holds the name of
     * the file.
     */
    private static ArrayList<String> _removalStage;

    /**
     * The filepath for the addition stage. All files to
     * be added will have BLOBS stored here.
     */
    private static final File ADDITION_PATH =
            new File(".gitlet/addition_stage");

    /**
     * The filepath for the removal stage. All files to
     * be removed at next commit will have their names stored
     * here.
     */
    private static final File REMOVAL_PATH = new File(".gitlet/removal_stage");

    /**
     * The OVERALL head pointer. This filepath always leads to
     * the current commit object.
     */
    private static File head = new File(".gitlet/HEAD");

    /**
     * The filepath to a file which contains the name of
     * the current branch.
     */
    private static File currBranch = new File(".gitlet/curr_branch");

    /**
     * This directory stores all BLOBS inside an inner directory
     * named after the commit they are relevant to.
     */
    private static File blobStore = new File(".gitlet/blob_storage");

    /**
     * The filepath to a directory of all commit objects that have been
     * made, ignoring branches.
     */
    private static File allComm = new File(".gitlet/all_comm");

    /**
     * The filepath to a directory with a file for each of the branches
     * in the gitlet repo. This only stores the name of the branches.
     */
    private static File allBranch = new File(".gitlet/all_branch");
}
