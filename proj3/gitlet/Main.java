package gitlet;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Kyle Casbon
 */

public class Main {
    
    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        switch (args[0]) {
        case "init":
            checkArgs(1, args);
            init();
            System.exit(0);
        default:
        }
        if (Repository.getGitletFolder().exists()) {
            switch (args[0]) {
            case "add":
                checkArgs(2, args);
                add(args);
                System.exit(0);
            case "rm":
                checkArgs(2, args);
                rm(args);
                System.exit(0);
            case "commit":
                checkArgs(2, args);
                commit(args);
                System.exit(0);
            case "branch":
                checkArgs(2, args);
                branch(args);
                System.exit(0);
            case "rm-branch":
                checkArgs(2, args);
                rmBranch(args[1]);
                System.exit(0);
            case "checkout":
                checkout(args);
            case "log":
                checkArgs(1, args);
                log();
                System.exit(0);
            case "global-log":
                checkArgs(1, args);
                globalLog();
                System.exit(0);
            case "status":
                checkArgs(1, args);
                status();
            case "find":
                checkArgs(2, args);
                find(args[1]);
                System.exit(0);
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
            }
        } else {
            System.out.println("Not in an initialized Gitlet directory.");
        }
    }

    static void init() {
        if (Repository.getGitletFolder().exists()) {
            System.out.println("A Gitlet version-control system "
                    +
                    "already exists in the current directory.");
            System.exit(0);
        }
        Date currDate = new Date(0);
        Repository repo = new Repository();
        Branch master = new Branch("master");
        _init = new Commit(currDate, "initial commit",
                new ArrayList<>(), new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    static void add(String... args) {
        if (!Utils.join(CWD, args[1]).exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        ArrayList<String> remStag =
                Utils.readObject(Repository.getRemovalPath(),
                        ArrayList.class);
        for (String file : remStag) {
            if (file.equals(args[1])) {
                remStag.remove(file);
            }
        }
        Utils.writeObject(Repository.getRemovalPath(), remStag);
        File currFileState = Utils.join(CWD, args[1]);
        List<String> allBlobs =
                Utils.plainFilenamesIn
                        (Repository.getBlobsFolder());
        ArrayList<String> blobArr = new ArrayList<>();
        blobArr.addAll(allBlobs);
        for (String blobID : blobArr) {
            File blobPath =
                    Utils.join(Repository.getBlobsFolder(),
                            blobID);
            Blob blob = Utils.readObject(blobPath, Blob.class);
            if (blob.getContents().equals
                    (Utils.readContentsAsString(currFileState))) {
                System.exit(0);
            }
        }
        Blob temp = new Blob(new File(args[1]));
    }

    @SuppressWarnings("unchecked")
    static void rm(String... args) {
        ArrayList<Blob> additStag;
        Boolean rmFile = false;
        additStag =
                Utils.readObject(Repository.getAdditionPath(),
                        ArrayList.class);
        for (Blob blob : additStag) {
            if (blob.getFile().toString().equals(args[1])) {
                _remBlob = blob;
                rmFile = true;
            }
        }
        additStag.remove(_remBlob);
        Utils.writeObject(Repository.getAdditionPath(),
                additStag);
        Commit recComm = getCurrCommit();
        File curCom = Utils.join(Repository.getBlobStore(),
                recComm.createID());
        File checkFile = Utils.join(curCom, args[1]);
        if (checkFile.exists()) {
            ArrayList<String> remStage;
            remStage =
                    Utils.readObject(Repository.getRemovalPath(),
                            ArrayList.class);
            remStage.add(args[1]);
            Utils.writeObject(Repository.getRemovalPath(),
                    remStage);
            rmFile = true;
            File cwdFile = Utils.join(CWD, args[1]);
            Utils.restrictedDelete(cwdFile);
        }
        if (!rmFile) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    static void branch(String... args) {
        List<String> allBranchList =
                Utils.plainFilenamesIn(Repository.getAllBranch());
        ArrayList<String> allBranchArr = new ArrayList<>();
        allBranchArr.addAll(allBranchList);
        for (String branch : allBranchArr) {
            if (branch.equals(args[1])) {
                System.out.println("A branch with that "
                        + "name already exists.");
                System.exit(0);
            }
        }
        Branch newBranch = new Branch(args[1]);
    }

    @SuppressWarnings("unchecked")
    static void commit(String... args) {
        if (args[1].equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        ArrayList<Blob> addStage;
        ArrayList<String> remStag;
        addStage =
                Utils.readObject(Repository.getAdditionPath(),
                        ArrayList.class);
        remStag =
                Utils.readObject(Repository.getRemovalPath(),
                        ArrayList.class);
        if (addStage.isEmpty()
                && remStag.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Date currDate = new Date();
        new Commit(currDate, args[1], addStage, remStag);
    }

    static void checkout(String... args) {
        if (args.length == 3) {
            String arg2 = args[2];
            Commit currComm = getCurrCommit();
            currComm.checkoutBasic(arg2, currComm);
            System.exit(0);
        }
        if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            checkout1(args);
            System.exit(0);
        }
        if (args.length == 2) {
            checkArgs(2, args);
            checkout2(args);
            System.exit(0);
        }
    }

    static void checkout1(String... args) {
        String commID = args[1];
        File checkID =
                Utils.join(Repository.getAllComm(),
                        commID);
        if (!checkID.exists()) {
            System.out.println("No commit with that id "
                    + "exists.");
            System.exit(0);
        }
        String file = args[3];
        File findCurr =
                Utils.join(Repository.getAllComm(),
                        commID);
        Commit curr =
                Utils.readObject(findCurr,
                        Commit.class);
        curr.checkoutOld(file, commID);
    }

    @SuppressWarnings("unchecked")
    static void checkout2(String... args) {
        String branch = args[1];
        String currBranch =
                Utils.readContentsAsString(Repository.getCurrBranch());
        if (branch.equals(currBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File newBranch = new File(".gitlet/all_branch/" + branch);
        if (!newBranch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }



        File currPath = new File(Repository.getBlobStore()
                + "/" + getCurrCommit().createID());
        File newCurrBranHead = Utils.join(Repository.getBranchesFolder(),
                branch, "HEAD");
        String newHead = Utils.readContentsAsString(newCurrBranHead);
        Utils.writeContents(Repository.getCurrBranch(), branch);
        Utils.writeContents(Repository.getHead(), newHead);
        File newPath = new File(Repository.getBlobStore()
                + "/" + getCurrCommit().createID());
        List<String> oldBlobs =
                Utils.plainFilenamesIn(currPath);
        if (oldBlobs != null) {
            ArrayList<String> oldBlobArr = new ArrayList<>();
            oldBlobArr.addAll(oldBlobs);
            for (String blob : oldBlobs) {
                File blobPath = Utils.join(Repository.getBlobsFolder(), blob);
                if (blobPath.exists()) {
                    String blobFile = Utils.readObject(blobPath, Blob.class).getFile().toString();
                    File oldBlob = Utils.join(CWD, blobFile);
                    File checkOldBlob = new File(".gitlet/blob_storage/" + branch + "/" + blob);
                    if (!checkOldBlob.exists()) {
                        Utils.restrictedDelete(oldBlob);
                    }
                }
            }
        }
        List<String> newBlobs =
                Utils.plainFilenamesIn(newPath);
        if (newBlobs != null) {
            ArrayList<String> newBlobArr = new ArrayList<>();
            newBlobArr.addAll(newBlobs);
            File findCurr =
                    Utils.join(Repository.getAllComm(),
                            newHead);
            Commit curr =
                    Utils.readObject(findCurr,
                            Commit.class);
            for (String blob : newBlobs) {
                File blobPath = Utils.join(Repository.getBlobsFolder(), blob);
                String blobFile = Utils.readObject(blobPath, Blob.class).getFile().toString();
                curr.checkoutOld(blobFile, newHead);
            }
        }


        ArrayList<Blob> addStage;
        ArrayList<String> remStag;
        addStage =
                Utils.readObject(Repository.getAdditionPath(),
                        ArrayList.class);
        remStag =
                Utils.readObject(Repository.getRemovalPath(),
                        ArrayList.class);
        addStage.clear();
        remStag.clear();
        Utils.writeObject(Repository.getAdditionPath(), addStage);
        Utils.writeObject(Repository.getRemovalPath(), remStag);
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    static void status() {
        ArrayList<Blob> addStag;
        ArrayList<String> remSta;
        addStag =
                Utils.readObject(Repository.getAdditionPath(),
                        ArrayList.class);
        remSta =
                Utils.readObject(Repository.getRemovalPath(),
                        ArrayList.class);

        List<String> allBranchList = Utils.plainFilenamesIn
                (Repository.getAllBranch());
        ArrayList<String> allBranchArr = new ArrayList<>();
        allBranchArr.addAll(allBranchList);
        String currBranch = Utils.readContentsAsString
                (Repository.getCurrBranch());
        System.out.println("=== Branches ===");
        for (String branch : allBranchArr) {
            if (branch.equals(currBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (Blob blob : addStag) {
            System.out.println(blob.getFile());
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String file : remSta) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications"
                + " Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
        System.exit(0);
    }

    static void find(String id) {
        Boolean messExist = false;
        List<String> commList =
                Utils.plainFilenamesIn(Repository.getAllComm());
        ArrayList<String> commArr = new ArrayList<>();
        commArr.addAll(commList);
        for (String comm : commArr) {
            File commPath =
                    Utils.join(Repository.getAllComm(), comm);
            Commit nextComm =
                    Utils.readObject(commPath, Commit.class);
            if (nextComm.getMessage().equals(id)) {
                System.out.println(nextComm.getID());
                messExist = true;
            }
        }
        if (!messExist) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    static void globalLog() {
        List<String> allCommList =
                Utils.plainFilenamesIn(Repository.getAllComm());
        ArrayList<String> allCommArr = new ArrayList<>();
        allCommArr.addAll(allCommList);
        for (String comm : allCommArr) {
            File commPath =
                    Utils.join(Repository.getAllComm(), comm);
            Commit commToBeLog =
                    Utils.readObject(commPath, Commit.class);
            System.out.println(commToBeLog.commitToString());
        }
    }

    static void log() {
        Commit currComm = getCurrCommit();
        while (currComm.getParent() != null) {
            System.out.println(currComm.commitToString());
            String curr = currComm.getParent();
            File comm =
                    Utils.join(Repository.getAllComm(), curr);
            currComm = Utils.readObject(comm, Commit.class);
        }
        System.out.println(currComm.commitToString());
    }

    static void rmBranch(String branch) {
        String activeBranch =
                Utils.readContentsAsString
                        (Repository.getCurrBranch());
        if (activeBranch.equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File delBranch =
                Utils.join(Repository.getBranchesFolder(),
                        branch);
        if (delBranch.exists()) {
            List<String> delFiles =
                    Utils.plainFilenamesIn(delBranch);
            ArrayList<String> allDelFiles = new ArrayList<>();
            allDelFiles.addAll(delFiles);
            for (String file : allDelFiles) {
                Utils.join(delBranch, file).delete();
            }
            delBranch.delete();
            File allBranchPath =
                    Utils.join(Repository.getAllBranch(), branch);
            allBranchPath.delete();
        } else {
            System.out.println("A branch with "
                    + "that name does not exist.");
        }
    }

    static Commit getCurrCommit() {
        String curr = Utils.readContentsAsString(Repository.getHead());
        File branch = Utils.join
                (Repository.getBranchesFolder(),
                        Utils.readContentsAsString(Repository.getCurrBranch()));
        File comm = Utils.join(branch, curr);
        Commit currComm = Utils.readObject(comm, Commit.class);
        return currComm;
    }

    static void checkArgs(int argLen, String... args) {
        if (argLen != args.length) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }


    /**
     * The current working directory that the program is being run in.
     */
    static final File CWD = new File(".");

    /**
     * The initial commit object, created when "init" is called.
     */
    private static Commit _init;

    /**
     * The blob to be removed in the "rm" call.
     */
    private static Blob _remBlob;
}
