package gitlet;

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
    /** The blobs directory. */
    static final File BLOBS = join(GITLETDIR, "blobs");
    static final File BRANCH = join(GITLETDIR, "branch");
    static final File COMMITS = join(GITLETDIR, "commits");
    /** The stage directory. */
    static final File STAGE = join(GITLETDIR, "stage");
    static final File STAGEADD = join(STAGE, "add");
    static final File STAGEREMOVE = join(STAGE, "remove");
    static final File STAGEREMOVEFILES = join(STAGEREMOVE, toFileName("removedFiles"));
    static final String AUTHOR = "XiaomingQiu";
    static final String DATAFILETYPE = "txt";

//    /** files in stage directory*/
//    static HashSet<String> filesInStageAdd;
//    static HashSet<String> filesInStageRemove;
//    static Commit INITIAL;
//    /** initial repository is not exist */
    public static void initial() throws IOException {
        GITLETDIR.mkdir();
        BRANCH.mkdir();
        COMMITS.mkdir();
        STAGE.mkdir();
        STAGEADD.mkdir();
        STAGEREMOVE.mkdir();
        STAGEREMOVEFILES.createNewFile();
        writeObject(STAGEREMOVEFILES, new HashSet<String>());
        BLOBS.mkdir();
        Commit initial_commit = new Commit("initial commit", AUTHOR, new Date(0), null, null, new HashMap<>());
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
        Commit commit = new Commit(meg, AUTHOR, new Date(), branch.getCurrentCommitId(), p2, files);
        byte[] content = Utils.serialize(commit);
        String commitId = Utils.sha1(content);
        File file = Utils.join(COMMITS, Commit.toFileName(commitId));
    }
    public static String toFileName(String name) {
        return name + "." + DATAFILETYPE;
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
    public static boolean sameWithBlob(File file, String blobName) {
        return getFileHashVal(file).equals(getFileNameNoEx(blobName));
    }
    /** get the extension of a file named fileName for example: ".txt"*/
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
        return Utils.sha1(contents, "file", getExtension(file));
    }
    /** get the file name in BLOBS: fileHashValue.ex*/
    public static String getBlobId(File file) {
        isFile(file);
        String fileName = file.getName();
        String blobId = getFileHashVal(file);
        int dot = fileName.lastIndexOf('.');
        return blobId + fileName.substring(dot);
    }
    public static void saveFileToBlob(File file) throws IOException {
        String blobName = getBlobId(file);
        File blobFile = join(BLOBS, blobName);
        if (blobFile.exists()) {
            copyFile(file, blobFile);
        }
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
            throw new GitletException(String.format("File %s is not a file.", file));
        }
    }
    public static void isDirectory(File file) {
        if (!file.isDirectory()) {
            throw new GitletException(String.format("File %s is not a directory.", file));
        }
    }
    public static void copyFile(File source, File target) throws IOException {
        creatFileIfNotExist(target);
        byte[] content = readContents(source);
        writeContents(target, content);
    }
    public static HashSet<String> getRemovedFilesSet() throws IOException {
        return readObject(STAGEREMOVEFILES, HashSet.class);
    }
    public static void saveRemovedFilesList(HashSet<String> removedFilesList) throws IOException {
        creatFileIfNotExist(STAGEREMOVEFILES);
        writeObject(STAGEREMOVEFILES, removedFilesList);
    }
    public static void clearStage() throws IOException {
        for (File file : STAGEADD.listFiles()) {
            file.delete();
        }
        creatFileIfNotExist(STAGEREMOVEFILES);
        writeObject(STAGEREMOVEFILES, new HashSet<String>());
    }
    public static File getBlob(String blobId) {
        return Utils.join(BLOBS, blobId);
    }
    public static byte[] getBlobContent(String blobId) {
        if (blobId.isEmpty()) {
            return new byte[0];
        } else {
            return Utils.readContents(getBlob(blobId));
        }
    }
    // merge two contents in current blob and given blob into targetFile
    public static void mergeToFile(String currentBlobId, String givenBlobId, File targetFile) throws IOException {
        if (targetFile.isFile()) {
            throw new GitletException(String.format("%s is not a file", targetFile));
        }
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        writeObject(targetFile, new Object[] {"<<<<<<< HEAD\n", getBlobContent(currentBlobId), "=======\n", getBlobContent(givenBlobId)});
    }
}
