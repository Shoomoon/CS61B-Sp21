package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    static final File GITLETDIR = join(CWD, ".gitlet");
    static final File COMMITS = join(GITLETDIR, "commits");
    /** The stage directory. */
    static final File STAGE = join(GITLETDIR, "stage");
    static final File STAGEADD = join(STAGE, "add");
    static final File STAGEREMOVEDIR = join(STAGE, "remove");
    static final File STAGEREMOVE = join(STAGEREMOVEDIR, toFileName("remove"));
    /** The blobs directory. */
    static final File BLOBS = join(GITLETDIR, "blobs");
    static final String AUTHOR = "XiaomingQiu";
    static final String DATAFILETYPE = "txt";

//    /** files in stage directory*/
//    static HashSet<String> filesInStageAdd;
//    static HashSet<String> filesInStageRemove;
//    static Commit INITIAL;
//    /** initial repository is not exist */
    public static void initial() throws IOException {
        GITLETDIR.mkdir();
        COMMITS.mkdir();
        STAGE.mkdir();
        STAGEADD.mkdir();
        STAGEREMOVEDIR.mkdir();
        STAGEREMOVE.createNewFile();
        BLOBS.mkdir();
        Commit initial_commit = new Commit("initial commit", AUTHOR, new Date(0), null, null, null);
        String initial_commitID = initial_commit.saveCommit();
        Branch branch = new Branch(initial_commitID, "master");
        branch.saveBranch();
    }
    /* TODO: fill in the rest of this class. */
    /** 1 add: store a copy of a file into STAGE directory, O(N + logN)
     * 1.1 read in current contents of the file and calculate the hash value, if not exist, throw error.
     * 1.2 compare to current working version, if same with current working version, don't stage, and remove if exist in STAGE
     * 1.3 remove previous copy in STAGE of same file if in STAGE;
     * 1.4 write in a new file in STAGE.
     * 1.5 add the new file into stage set
     *
     * 2 commit: Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit
     * 2.1
     * */
    public void addCommit(String meg, String p2, HashMap<String, String> files) {
        Branch branch = Branch.getBranch();
        Commit commit = new Commit(meg, AUTHOR, new Date(), branch.currentBranchName(), p2, files);
        byte[] content = Utils.serialize(commit);
        String commitId = Utils.sha1((Object) content);
        File file = Utils.join(COMMITS, Commit.toFileName(commitId));
    }
    public File getCommitFile(String commitId) {
        return Utils.join(COMMITS, Commit.toFileName(commitId));
    }
    public Commit getCommit(String commitId) {
        File commitFile = getCommitFile(commitId);
        if (!commitFile.exists()) {
            return null;
        }
        return readObject(commitFile, Commit.class);
    }
    public static String toFileName(String name) {
        return name + "." + DATAFILETYPE;
    }
    public static void checkFile(File file) {
        if (!file.exists()) {
            throw new GitletException(String.format("%s not exists.", file.toString()));
        }
    }
    public static void creatFileIfNotExist(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
    }
    /** check if file0 has the same value with file1*/
    public static boolean sameFile(File file0, File file1) {
        return getFileHashVal(file0).equals(getFileHashVal(file1));
    }
    /** get the extension of a file named fileName*/
    public static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot >= 0) {
            return fileName.substring(dot);
        }
        return "";
    }
    /** get the extension of a file*/
    public static String getExtension(File file) {
        String fileName = file.getName();
        return getExtension(fileName);
    }
    /** get the hash value of a file, combine with "file" and the extension*/
    public static String getFileHashVal(File file) {
        isFile(file);
        if (!file.exists()) { return "";}
        byte[] contents = Utils.readContents(file);
        return Utils.sha1((Object) contents, "file", getExtension(file));
    }
    /** add the extension of file to fileId*/
    public static String toFileNameWithEx(File file, String fileId) {
        isFile(file);
        String fileName = file.getName();
        return toFileNameWithEx(fileName, fileId);
    }
    /** get the file name in BLOBS: fileHashValue.ex */
    public static String getFileNameWithEx(File file) {
        isFile(file);
        String fileId = getFileHashVal(file);
        return toFileNameWithEx(file, fileId);
    }
    /** add the extension of fileName to fileId*/
    public static String toFileNameWithEx(String fileName, String fileId) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            throw new GitletException(String.format("%s is not a file.", fileName));
        }
        return fileId + fileName.substring(dot);
    }
    public static String getFileNameNoEx(String fileName) {
        if (fileName != null && fileName.length() > 0) {
            int dot = fileName.lastIndexOf('.');
            if (dot > -1) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }
    public static void isFile(File file) {
        if (!file.isFile()) {
            throw new GitletException(String.format("File %s is not a file.", file.toString()));
        }
    }
    public static void isDirectory(File file) {
        if (!file.isDirectory()) {
            throw new GitletException(String.format("File %s is not a directory.", file.toString()));
        }
    }
    public static void copyFile(File source, File target) throws IOException {
        isFile(source);
        isFile(target);
        if(!source.exists()) {
            throw new GitletException(String.format("File %s not exists.", source.toString()));
        }
        creatFileIfNotExist(target);
        byte[] content = readContents(source);
        writeContents(target, (Object) content);
    }
    public static HashSet<String> getRemovedFilesList() {
        File removedFiles = join(STAGEREMOVE, toFileName("removedFiles"));
        if (!removedFiles.exists()) {
            return new HashSet<String>();
        }
        return readObject(removedFiles, HashSet.class);
    }
    public static void saveRemovedFilesList(HashSet<String> removedFilesList) {
        File removedFiles = join(STAGEREMOVE, toFileName("removedFiles"));
        writeObject(removedFiles, removedFilesList);
    }
    public static void clearStage() {
        for (File file : STAGEADD.listFiles()) {
            file.delete();
        }
        File removedFiles = join(STAGEREMOVE, toFileName("removedFiles"));
        writeObject(removedFiles, new HashSet<String>());
    }
}
