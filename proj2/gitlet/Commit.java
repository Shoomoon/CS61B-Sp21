package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** Commit ID == Commit hash value*/
    /** The message of this Commit. */
    private String message;
    private String author;
    private Date commitTime;
    private String firstParent;
    private String secondParent;
    static final String DATAFILETYPE = Repository.DATAFILETYPE;
    static final File BLOBS = Repository.BLOBS;
    static final int COMMITIDLENGTH = 7;
    /** fileName maps to blobId, both including extension*/
    private HashMap<String, String> trackedFiles;
    static final File COMMITS = Repository.COMMITS;
    /* TODO: fill in the rest of this class. */
    public Commit(String meg, String aut, Date date, String p1, String p2, HashMap<String, String> files) {
        message = meg;
        author = aut;
        commitTime = date;
        firstParent = p1;
        secondParent = p2;
        trackedFiles = files;
    }
    /** get hash value of the commit, combine the content with "commit", no extension*/
    public String getCommitId() {
        byte[] content = Utils.serialize(this);
        return Utils.sha1((Object) content, "commit", DATAFILETYPE);
    }
    /** get the file ID of a file, extension included*/
    public String getBlobId(String fileName) {
//        if (!trackedFiles.containsKey(fileName)) {
//            return "";
//        }
        return trackedFiles.getOrDefault(fileName, "");
    }
    /** get the file that this commit is tracking by the name of the file (extension included)*/
    public File getBlob(String fileName) {
        String blobId = getBlobId(fileName);
        return Utils.join(BLOBS, blobId);
    }
    public static void exists(String commitId) {
        if (commitId == null) {
            throw new GitletException("Commit should not be empty.");
        }
        File commitFile = join(COMMITS, toFileName(commitId));
        exists(commitFile);
    }
    public static void exists(File commitFile) {
        if (!commitFile.exists()) {
            throw new GitletException("No commit with that id exists.");
        }
    }
    /** get the parent 1*/
    public String getFirstParent() {
        return firstParent;
    }
    public String getSecondParent() { return secondParent; }
    public Date getCommitTime() { return commitTime; }
    public String getMessage() {
        return message;
    }
    /** get the tracking files hash map*/
    public HashMap<String, String> getTrackedFiles() {
        return trackedFiles;
    }
    public static String toFileName(String fileName) {
        return fileName + "." + DATAFILETYPE;
    }
    /** get the commit from commitId*/
    public static Commit fromCommitId(String commitId) {
        exists(commitId);
        File commitFile = join(COMMITS, toFileName(commitId));
        return readObject(commitFile, Commit.class);
    }
    public static Commit fromFile(File commitFile) {
        exists(commitFile);
        return readObject(commitFile, Commit.class);
    }
    /** Save the commit and return the commit ID*/
    public String saveCommit() throws IOException {
        byte[] content = Utils.serialize(this);
        String commitId = getCommitId();
        File file = join(COMMITS, toFileName(commitId));
        if (!file.exists()) {
            file.createNewFile();
        }
        writeContents(file, content);
        return commitId;
    }
    public static void printCommit(File commitFile) {
        Commit commit = readObject(commitFile, Commit.class);
        commit.printCommit(Repository.getFileNameNoEx(commitFile.getName()));
    }
    /** print commit by commit ID(no extension)*/
    public void printCommit(String commitId) {
        System.out.print("===\n");
        System.out.printf("commit %s\n", commitId);
        if(secondParent != null && !secondParent.isEmpty()) {
            System.out.printf("Merge: %s, %s\n", firstParent.substring(0, COMMITIDLENGTH), secondParent.substring(0, COMMITIDLENGTH));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss yyyy Z");
        System.out.printf("Date: %s\n", dateFormat.format(commitTime).toString());
        System.out.printf("%s\n\n", message);
    }
    public static void printCommitById(String commitId) {
        Commit commit = Commit.fromCommitId(commitId);
        commit.printCommit(commitId);
    }
    public boolean isTracked(String fileNameWithEx) {
        return fileNameWithEx != null && trackedFiles.containsKey(fileNameWithEx);
    }
    public static String lastestCommonAncestorCommitId(String commitId0, String commitId1) {
        String commitId0_init = commitId0;
        String commitId1_init = commitId1;
        while (!commitId0.equals(commitId1)) {
            Commit commit0 = fromCommitId(commitId0);
            commitId0 = commit0.getFirstParent();
            if (commitId0.isEmpty()) {
                commitId0 = commitId1_init;
            }
            Commit commit1 = fromCommitId(commitId1);
            commitId1 = commit1.getFirstParent();
            if (commitId1.isEmpty()) {
                commitId1 = commitId0_init;
            }
        }
        return commitId0;
    }
}
