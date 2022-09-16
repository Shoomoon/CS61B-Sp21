package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

public class Branch implements Serializable {
    /** branchName maps to commitId(commit hash value) */
    private HashMap<String, String> branchToHeadCommits;

    /** The head of the branch(current branch name). */
    private String currentBranchName;
    /** fileId = file SH1 hash value */
//    private HashMap<String, String> filesIdToHashVal;
    static final String DATAFILETYPE = Repository.DATAFILETYPE;
    static final File BRANCH = Repository.BRANCH;
    public Branch() {
        branchToHeadCommits = new HashMap<>();
        currentBranchName = "";
    }
    public Branch(String commitId, String branchName) {
        currentBranchName = branchName;
        branchToHeadCommits = new HashMap<>();
        branchToHeadCommits.put(branchName, commitId);
    }
    public static String toFileName(String name) {
        return name + "." + DATAFILETYPE;
    }
    public String getCurrentCommitId() {
        if (currentBranchName == null) {
            throw new GitletException("Branch not named.");
        }
        return branchToHeadCommits.get(currentBranchName);
    }
    public Commit getCurrentCommit() {
        return getBranchHeadCommit(currentBranchName);
    }
    public String currentBranchName() {
        return currentBranchName;
    }
    public Commit getBranchHeadCommit(String branchName) {
        String commitId = branchToHeadCommits.get(branchName);
        return Commit.fromCommitId(commitId);
    }
    public void setCurrentBranchName(String branchName) throws IOException {
        if (branchName == null) {
            throw new GitletException("Branch name should not be empty.");
        }
        if (!branchToHeadCommits.containsKey(branchName)) {
            throw new GitletException("Branch name not exists.");
        }
        currentBranchName = branchName;
        saveBranch();
    }
    public void forwardCurrentBranchTo(String commitId) throws IOException {
        branchToHeadCommits.put(currentBranchName, commitId);
        saveBranch();
    }
    public void newBranch(String commitId, String branchName) throws IOException {
        Commit.exists(commitId);
        if (branchName == null) {
            throw new GitletException("Branch name should not be empty.");
        }
        if (branchToHeadCommits.containsKey(branchName)) {
            throw new GitletException(String.format("Branch %s has already exist.", branchName));
        }
        branchToHeadCommits.put(branchName, commitId);
        saveBranch();
    }
    public void newBranch(String branchName) throws IOException {
        newBranch(getCurrentCommitId(), branchName);
    }
    public void addCommitToCurrentBranch(String meg, String author, String p2, HashMap<String, String> files) throws IOException {
        Commit commit = new Commit(meg, author, new Date(), getCurrentCommitId(), p2, files);
        String commitId = commit.saveCommit();
        if (currentBranchName == null || currentBranchName.equals("")) {
            throw new GitletException("Add a new branch to current commit.");
        }
        branchToHeadCommits.put(currentBranchName, commitId);
        saveBranch();
    }
    public void addCommitToCurrentBranch(String commitId) throws IOException {
        Commit.exists(commitId);
        branchToHeadCommits.put(currentBranchName, commitId);
        saveBranch();
    }
    public static Branch fromFile(File file) {
        return readObject(file, Branch.class);
    }
    public static Branch getBranch() {
        File branchFile = join(BRANCH, toFileName("branch"));
        return fromFile(branchFile);
    }
    public void saveBranch() throws IOException {
        File file = join(BRANCH, toFileName("branch"));
        if (!file.exists()) {
            file.createNewFile();
        }
        writeObject(file, this);
    }
    public ArrayList<String> getAllBranchNames() {
        return new ArrayList<String>(branchToHeadCommits.keySet());
    }
    public boolean containBranch(String branchName) {
        return branchToHeadCommits.containsKey(branchName);
    }
    public String getCurrentBranchName() {
        return currentBranchName;
    }
    public void removeBranch(String branchName) throws IOException {
        if (branchToHeadCommits.containsKey(branchName)) {
            branchToHeadCommits.remove(branchName);
            saveBranch();
        }
    }
}
