package gitlet;

import java.io.File;
import java.io.IOException;

public class Branch {

    Branch(String name) {
        _name = name;
        File newBranch = Utils.join(Repository.getBranchesFolder(), _name);
        newBranch.mkdir();
        File allBranch = new File(".gitlet/all_branch/" + _name);
        try {
            allBranch.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        Utils.writeContents(allBranch, _name);

        File branchHead = new File(".gitlet/branches/" + getName() + "/HEAD");
        try {
            branchHead.createNewFile();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        if (!getName().equals("master")) {
            Utils.writeContents(branchHead, Main.getCurrCommit().createID());
            File firstCommit = new File(".gitlet/branches/" + getName() + "/" + Utils.readContentsAsString(Repository.getHead()));
            try {
                firstCommit.createNewFile();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
            File comm = new File(".gitlet/all_comm/" + Utils.readContentsAsString(Repository.getHead()));
            Commit initCom = Utils.readObject(comm, Commit.class);
            Utils.writeObject(firstCommit, initCom);
        }
    }

    void switchBranch() {
        Utils.writeContents(Repository.getCurrBranch(), this.getName());
    }

    String getName() {
        return _name;
    }

    /**
     * The name of a given branch.
     */
    private static String _name;

}
