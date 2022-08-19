package gitlet;

// TODO: any imports you need here

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
    private String parent1;
    private String parent2;
    static final String DATAFILETYPE = Branch.DATAFILETYPE;
    static final File BLOBS = Repository.BLOBS;
    static final int COMMITIDLENGTH = 7;
    /** fileName maps to fileId, including extension*/
    private HashMap<String, String> trackedFiles;
    static final File COMMITS = Repository.COMMITS;
    /* TODO: fill in the rest of this class. */
    public Commit(String meg, String aut, Date date, String p1, String p2, HashMap<String, String> files) {
        message = meg;
        author = aut;
        commitTime = date;
        parent1 = p1;
        parent2 = p2;
        trackedFiles = files;
    }
    /** get hash value of the commit, combine the content with "commit"*/
    public String getCommitId() {
        byte[] content = Utils.serialize(this);
        return Utils.sha1((Object) content, "commit");
    }
    /** get the file ID of a file, extension included*/
    public String getFileId(String fileName) {
        if (!trackedFiles.containsKey(fileName)) {
            return null;
        }
        return trackedFiles.get(fileName);
    }
    /** get the file that this commit is tracking by the name of the file (extension included)*/
    public File getFile(String fileName) {
        if (!trackedFiles.containsKey(fileName)) {
            return null;
        }
        String fileId = trackedFiles.get(fileName);
        return Utils.join(BLOBS, fileId);
    }
    /***/
    public String getParent1() {
        return parent1;
    }
    /** get the */
    public HashMap<String, String> getTrackedFiles() {
        return trackedFiles;
    }
    public static String toFileName(String fileName) {
        return fileName + "." + DATAFILETYPE;
    }
    public static Commit fromFile(String commitId) {
        File commitFile = join(COMMITS, toFileName(commitId));
        return fromFile(commitFile);
    }
    public static Commit fromFile(File commitFile) {
        if (!commitFile.exists()) {
            throw new GitletException(String.format("Commit %s not exists.", commitFile.toString()));
        }
        return readObject(commitFile, Commit.class);
    }
    /** Save the commit and return the commit ID*/
    public String saveCommit() throws IOException {
        byte[] content = Utils.serialize(this);
        String commitId = Utils.sha1(content);
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
    public void printCommit(String commitFileName) {
        System.out.print("===\n");
        System.out.printf("commit %s\n", commitFileName);
        if(parent2 != null) {
            System.out.printf("Merge: %s, %s\n", parent1.substring(0, COMMITIDLENGTH), parent2.substring(0, COMMITIDLENGTH));
        }
        System.out.printf("Date: %s\n", commitTime.toString());
        System.out.printf("%s\n\n", message);
    }
    public String getMessage() {
        return message;
    }
    public boolean isTracked(String fileNameWithEx) {
        return trackedFiles.containsKey(fileNameWithEx);
    }
}
