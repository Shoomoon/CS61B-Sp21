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

    /** The commits directory. */
    private String headCommitId;
    /** fileId = file SH1 hash value */
//    private HashMap<String, String> filesIdToHashVal;
    static final String DATAFILETYPE = Repository.DATAFILETYPE;
    static final File BRANCHDIR = Repository.GITLETDIR;
    static final File COMMITS = Repository.COMMITS;
    public Branch() {
        branchToHeadCommits = new HashMap<>();
        headCommitId = "";
    }
    public Branch(String commitId, String branchName) {
        headCommitId = branchName;
        branchToHeadCommits = new HashMap<>();
        branchToHeadCommits.put(branchName, commitId);
    }
    public static String toFileName(String name) {
        return name + "." + DATAFILETYPE;
    }
    public String getCurrentCommitId() {
        if (headCommitId == null) {
            throw new GitletException("Branch not initiated.");
        }
        return branchToHeadCommits.get(headCommitId);
    }
    public Commit getCurrentCommit() {
        String commitId = getCurrentCommitId();
        return Commit.fromFile(commitId);
    }
    public String currentBranchName() {
        return headCommitId;
    }
    public Commit getBranchHeadCommit(String branchName) {
        String commitId = branchToHeadCommits.get(branchName);
        File commitFile = join(COMMITS, Commit.toFileName(commitId));
        return readObject(commitFile, Commit.class);
    }
    public void setCurrentBranch(String branchName) throws IOException {
        if (branchName == null) {
            throw new GitletException("Branch name should not be empty.");
        }
        if (!branchToHeadCommits.containsKey(branchName)) {
            throw new GitletException("Branch name not exists.");
        }
        headCommitId = branchName;
        saveBranch();
    }
    public void forwardCurrentBranchTo(String commitId) throws IOException {
        branchToHeadCommits.put(headCommitId, commitId);
        saveBranch();
    }
    public void newBranch(String commitId, String branchName) throws IOException {
        if (commitId == null) {
            throw new GitletException("Commit should not be empty.");
        }
        File commitFile = join(COMMITS, Commit.toFileName(commitId));
        if (!commitFile.exists()) {
            throw new GitletException(String.format("Commit %s not exists", commitId));
        }
        if (branchName == null) {
            throw new GitletException("Name should not be empty.");
        }
        if (branchToHeadCommits.containsKey(branchName)) {
            throw new GitletException(String.format("Branch %s has already exist.", branchName));
        }
        branchToHeadCommits.put(branchName, commitId);
        saveBranch();
    }
    public void addCommitToCurrentBranch(String meg, String author, String p2, HashMap<String, String> files) throws IOException {
        Commit commit = new Commit(meg, author, new Date(), getCurrentCommitId(), p2, files);
        String commitId = commit.saveCommit();
        branchToHeadCommits.put(headCommitId, commitId);
        saveBranch();
    }
    public static Branch fromFile(File file) {
        return readObject(file, Branch.class);
    }
    public static Branch getBranch() {
        File branchFile = join(BRANCHDIR, toFileName("branch"));
        return readObject(branchFile, Branch.class);
    }
    public void saveBranch() throws IOException {
        File file = join(BRANCHDIR, toFileName("branch"));
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
    public String getHeadCommitId() {
        return headCommitId;
    }
    public void removeBranch(String branchName) throws IOException {
        if (branchToHeadCommits.containsKey(branchName)) {
            branchToHeadCommits.remove(branchName);
            saveBranch();
        }
    }
}
