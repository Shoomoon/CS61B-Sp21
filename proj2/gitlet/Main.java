package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws GitletException, IOException {
        // TODO: what if args is empty?
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Please enter a command.");
        }
        String firstArg = args[0];
        validateArgs(firstArg, args);
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                // initiallize the repository
                // TODO: fill the rest for "init" command
                /** 1. "init": Creates a new Gitlet version-control system in the current directory with a initial commit.
                 * 1.1. if a Gitlet exists, throw error message and quit.
                 * 1.2. create directories.
                 * 1.3. create a branch named "master".
                 * 1.4. create a commit with "initial commit" message and date(0).
                 * 1.5. make the head pointer of new branch "master" points to this commit.*/
                File gitletDir = Repository.GITLETDIR;
                if (gitletDir.exists()) {
                    throw new GitletException("A Gitlet version-control system already exists in the current directory.");
                }
                Repository.initial();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                // Adds a copy of the file as it currently exists to the staging area.
                // only one file may be added at a time.
                // TODO: fill the rest for "add" command
                /** 2. "add": store a copy of a file into STAGE directory, O(N + logN)
                 * 2.1. read in current contents of the file and calculate the hash value, if not exist, throw error.
                 * 2.2. compare to current commit version, if same with current commit version, don't stage, and remove if exists in STAGEADD
                 * 2.3. otherwise, copy the file into STAGEADD;
                 * 2.4. if the file has been staged to remove, remove it from removedFileList */
                String fileName = args[1];
                File fileToAdd = new File(Repository.CWD, fileName);
                if (!fileToAdd.exists()) {
                    throw new GitletException("File does not exist.");
                }
                Branch branch = Branch.getBranch();
                Commit currentCommit = branch.getCurrentCommit();
                String blobId = currentCommit.getBlobId(fileName);
                File stagedFile = Utils.join(Repository.STAGEADD, fileName);
                if (Repository.sameWithBlob(fileToAdd, blobId)) {
                    if (stagedFile.exists()) {
                        stagedFile.delete();
                    }
                } else {
                    Repository.copyFile(fileToAdd, stagedFile);
                }
                //
                HashSet<String> removedFilesSet = Repository.getRemovedFilesSet();
                if (removedFilesSet.contains(fileName)) {
                    removedFilesSet.remove(fileName);
                    Repository.saveRemovedFilesList(removedFilesSet);
                }
                break;
            // TODO: FILL THE REST IN
            case "commit":
                // Saves a snapshot of tracked files in the current commit and staging area
                // so they can be restored at a later time, creating a new commit.
                // TODO:  fill the rest for "commit" command
                /** 3. "commit": Saves a snapshot of tracked files in the current commit and staging area, so they can be restored at a later time, creating a new commit
                 * 3.1. if no files in STAGE, throw error "No changes added to the commit."
                 * 3.2. if message is empty, throw error "Please enter a commit message.".
                 * 3.2. create snapshot for tracked files in current commit and files in STAGE, while those files marked as "remove".
                 * 3.3. create a new commit with that snapshot, message, and current date.
                 * 3.4. make the head pointer of current branch points to this commit.
                 * 3.5. clear STAGE.
                 * */
                Boolean addedOrRemoved = false;
                File[] filesInStageAdd = Repository.STAGEADD.listFiles();
                HashSet<String> filesInStageRemove = Repository.getRemovedFilesSet();
                String secondParentCommitId = "";
                if (args.length == 3) {
                    secondParentCommitId = args[2];
                }
                if (filesInStageAdd == null && filesInStageRemove == null) {
                    throw new GitletException("No changes added to the commit.");
                }
                String message = args[1];
                if (message == null || message.isEmpty()) {
                    throw new GitletException("Please enter a commit message.");
                }
                branch = Branch.getBranch();
                currentCommit = branch.getCurrentCommit();
                HashMap<String, String> trackedFiles = currentCommit.getTrackedFiles();
                for (File file: filesInStageAdd) {
                    String file_name = file.getName();
                    blobId = Repository.getBlobId(file);
                    trackedFiles.put(file_name, blobId);
                    File blobFile = Utils.join(Repository.BLOBS, blobId);
                    if (!blobFile.exists()) {
                        Repository.copyFile(file, blobFile);
                    }
                    addedOrRemoved = true;
                }
                for (String file_name: filesInStageRemove) {
                    trackedFiles.remove(file_name);
                    addedOrRemoved = true;
                }
                if (!addedOrRemoved) {
                    throw new GitletException("No changes added to the commit.");
                }
                branch.addCommitToCurrentBranch(message, Repository.AUTHOR, secondParentCommitId, trackedFiles);
                Repository.clearStage();
                break;
            case "rm":
                // Unstage the file if it is currently staged for addition.
                // If the file is tracked in the current commit,
                // stage it for removal and remove the file from
                // the working directory if the user has not already done so
                // (do not remove it unless it is tracked in the current commit).
                // TODO: fill the rest for "rm" command
                /** 4. "rm":
                 * 4.1. check if file in stageAdd directory, if so, delete it.
                 * 4.2. get tracked files by current commit
                 * 4.3. if the file is tracked by current commit, add it into removed files list and save.
                 * 4.4. delete the file from CWD
                 * 4.5. if the file is not staged nor tracked, print error message "No reason to remove the file."
                 * */
                boolean stagedOrTracked = false;
                fileName = args[1];
                File fileInStageAdd = Utils.join(Repository.STAGEADD, fileName);
                if (fileInStageAdd.exists()) {
                    stagedOrTracked = true;
                    fileInStageAdd.delete();
                }
                branch = Branch.getBranch();
                currentCommit = branch.getCurrentCommit();
                if (currentCommit.isTracked(fileName)) {
                    stagedOrTracked = true;
                    /** stage for removal*/
                    HashSet<String> removedFiles = Repository.getRemovedFilesSet();
                    removedFiles.add(fileName);
                    Repository.saveRemovedFilesList(removedFiles);
                    /** remove the file from CWD*/
                    File fileInCWD = Utils.join(Repository.CWD, fileName);
                    if (fileInCWD.exists()) {
                        fileInCWD.delete();
                    }
                }
                if(!stagedOrTracked) {
                    throw new GitletException("No reason to remove the file.");
                }
                break;
            case "log":
                // TODO: fill the rest for "log" command
                /** 5. "log": Starting from the current head commit, display information about each commit backwards along the commit tree until the initial commit
                 * 5.1. Starting at the current head commit, print commit SH1, commit time, and commit message.
                 * 5.2. for merge commits, add a line just below the first: "Merge: commitId_1, commitId_2".
                 *
                 * */
                branch = Branch.getBranch();
                String currentCommitId = branch.getCurrentCommitId();
                while (currentCommitId != null && !currentCommitId.isEmpty()) {
                    Commit commit = Commit.fromCommitId(currentCommitId);
                    commit.printCommit(currentCommitId);
                    currentCommitId = commit.getFirstParent();
                }
                break;

            case "global-log":
                /** 6. "global-log": displays information about all commits ever made. The order of the commits does not matter.
                 * 6.1. get all commit file names (first COMMITIDSIZE of commit SH1 value ending with ".txt").
                 * 6.2. print information of each commit same as "log" */
                File[] commitFiles = Repository.COMMITS.listFiles();
                for (File commitFile: commitFiles) {
                    if (!commitFile.isFile()) {
                        continue;
                    }
                    String commitId = Repository.getFileNameNoEx(commitFile.getName());
                    Commit commit = Commit.fromFile(commitFile);
                    commit.printCommit(commitId);
                }
                break;
            case "find":
                // Prints out the ids of all commits that have the given commit message, one per line.
                // If there are multiple such commits, it prints the ids out on separate lines.
                /** 7. "find": Prints out the ids of all commits that have the given commit message, one per line.
                 * 7.1. get all commit file names.
                 * 7.2. for each commit, if the message contains the given commit message, then print the commit SH1 value.
                 * 7.3. if not such commit exists, throw error "Found no commit with that message".
                 * */
                message = args[1];
                commitFiles = Repository.COMMITS.listFiles();
                boolean hasMessage = false;
                for (File commitFile: commitFiles) {
                    if (!commitFile.isFile()) {
                        continue;
                    }
                    Commit commit = Commit.fromFile(commitFile);
                    String commitId = Repository.getFileNameNoEx(commitFile.getName());
                    if (commit.getMessage().contains(message)) {
                        System.out.printf("%s\n", commitId);
                        hasMessage = true;
                    }
                }
                if (!hasMessage) {
                    System.out.print("Found no commit with that message.");
                }
                break;
            case "status":
                /** 8. "status": Displays what branches currently exist, and marks the current branch with a *.
                 * 8.1. print the names of all branches, among which marks the current branch with a "*".
                 * 8.2. print the names of files in stage_add set (print the names of files that have staged for addition).
                 * 8.3. print the names of files in stage_remove set (print the names of files that have staged for removal).
                 * 8.4. get all files' names in current working directory "CWD".
                 * 8.5. iterate all files, for files that exist in current commit, but not staged as add or remove, print the status(deleted or modified)
                 * 8.5. for files that not exist in current commit, print the files' names marked as "Untracked Files".
                 * */
                branch = Branch.getBranch();
                currentCommit = branch.getCurrentCommit();
                /** print branches names*/
                System.out.printf("=== Branches ===\n");
                List<String> branchNames = branch.getAllBranchNames();
                Collections.sort(branchNames);
                String currentBranchName = branch.getCurrentBranchName();
                for (String branchName: branchNames) {
                    if (branchName.equals(currentBranchName)) {
                        System.out.print("*");
                    }
                    System.out.printf("%s\n", branchName);
                }
                /** print files in Stage add directory */
                System.out.printf("\n=== Staged Files ===\n");
                List<String> addedFiles = Utils.plainFilenamesIn(Repository.STAGEADD);
                for (String file: addedFiles) {
                    System.out.printf("%s\n", file);
                }
                /** print removed files */
                System.out.print("\n=== Removed Files ===\n");
                removedFilesSet = Repository.getRemovedFilesSet();
                List<String> removedFiles = new ArrayList<>(removedFilesSet);
                Collections.sort(removedFiles);
                for (String removedFileName: removedFiles) {
                    System.out.printf("%s\n", removedFileName);
                }
                /** check files in CWD if they are modified but not staged for commit, or untracked */
                System.out.print("\n=== Modifications Not Staged For Commit ===\n");
                List<String> untrackedFiles = new LinkedList<String>();
                List<String> filesInCWD = Utils.plainFilenamesIn(Repository.CWD);
                for (String file_name: filesInCWD) {
                    File file = Utils.join(Repository.CWD, file_name);
                    fileInStageAdd = Utils.join(Repository.STAGEADD, file_name);
                    boolean staged = fileInStageAdd.exists() || removedFilesSet.contains(file_name);
                    if (currentCommit.isTracked(file_name) && !Repository.sameWithBlob(file, currentCommit.getBlobId(file_name)) && !staged
                            || fileInStageAdd.exists() && !Repository.sameFile(fileInStageAdd, file)) {
                        System.out.printf("%s (modified)\n", file_name);
                    } else if (fileInStageAdd.exists() && !file.exists()
                            || !removedFilesSet.contains(file_name) && currentCommit.isTracked(file_name) && !file.exists()) {
                        System.out.printf("%s (deleted)\n", file_name);
                    } else if (file.exists() && !fileInStageAdd.exists() && !currentCommit.isTracked(file_name)) {
                        untrackedFiles.add(file_name);
                    }
                }
                /** print untracked files*/
                System.out.print("\n=== Untracked Files ===\n");
                for (String file_name : untrackedFiles) {
                    System.out.printf("%s\n", file_name);
                }
                System.out.print("\n");
                break;
            case "checkout":
                /** 9. "checkout": checkout -- [file name]; checkout [commit id] -- [file name]; checkout [branch name].
                 * 9.1. checkout -- [file name].
                 * 9.1.1. if [file name] not in current commit, throw error "File does not exist in that commit".
                 * 9.1.2. if the file exists in current working directory, remove it.
                 * 9.1.3. copy [file name] to current working directory.
                 *
                 * 9.2. checkout -- [commit id] -- [file name]
                 * 9.2.1. if no commit with the given id exists, throw error "No commit with that id exists".
                 * 9.2.2. if [file name] not in [commit id], throw error "File does not exist in that commit".
                 * 9.2.3. if the file exists in current working directory, remove it.
                 * 9.2.4. copy [file name] in [commit id] to current working directory.
                 *
                 * 9.3. checkout [branch name]
                 * 9.3.1. If no branch with that name exists, throw error "No such branch exists."
                 * 9.3.2. If that branch is the current branch, throw error "No need to checkout the current branch."
                 * 9.3.3. get all files' names in current working directory "CWD".
                 * 9.3.4. if any file not in current branch but in [branch name]， throw error "There is an untracked file in the way; delete it, or add and commit it first."
                 * 9.3.5. clear current working directory
                 * 9.3.2. copy files in the commit at the head of [branch name] to current working directory.
                 * 9.3.3. if [branch name] is not same as the current branch name, clear STAGE.
                 * 9.3.4. make the [branch name] as the current branch.
                 * */
                branch = Branch.getBranch();
                if (args.length > 2) {
                    String commitId;
                    fileName = "";
                    File file = null;
                    if (args[1].equals("--")) {
                        fileName = args[2];
                        commitId = branch.getCurrentCommitId();
                    } else {
                        commitId = args[1];
                        fileName = args[3];
                    }
                    File commitFile = Utils.join(Commit.COMMITS, Commit.toFileName(commitId));
                    if (!commitFile.exists()) {
                        System.out.print("No commit with that id exists.");
                        break;
                    }
                    Commit commit = Commit.fromFile(commitFile);
                    if (!commit.isTracked(fileName)) {
                        System.out.print("File does not exist in that commit.");
                        break;
                    }
                    File blob = commit.getBlob(fileName);
                    Repository.copyFile(blob, Utils.join(Repository.CWD, fileName));
                } else {
                    String branchName = args[1];
                    if (!branch.containBranch(branchName)) {
                        System.out.print("No such branch exists.");
                        break;
                    }
                    if (branchName.equals(branch.getCurrentBranchName())) {
                        System.out.print("No need to checkout the current branch.");
                        break;
                    }
                    currentCommit = branch.getCurrentCommit();
                    Commit targetCommit = branch.getBranchHeadCommit(branchName);
                    List<String> files = Utils.plainFilenamesIn(Repository.CWD);
                    for (String fileNameInCWD : files) {
                        if (!currentCommit.isTracked(fileNameInCWD) && targetCommit.isTracked(fileNameInCWD)) {
                            throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
                        }
                    }
                    for (String fileTrackedInCurrentCommit : currentCommit.getTrackedFiles().keySet()) {
                        File file = Utils.join(Repository.CWD, fileTrackedInCurrentCommit);
                        file.delete();
                    }
                    Repository.clearStage();
                    HashMap<String, String> filesInTargetCommit = targetCommit.getTrackedFiles();
                    for (String fileTrackedInTargetCommit : filesInTargetCommit.keySet()) {
                        File source = Utils.join(Repository.BLOBS, filesInTargetCommit.get(fileTrackedInTargetCommit));
                        File target = Utils.join(Repository.CWD, fileTrackedInTargetCommit);
                        Repository.copyFile(source, target);
                    }
                    branch.setCurrentBranchName(branchName);
                }
                break;
            case "branch":
                /** 10. "branch": Creates a new branch with the given name, and points it at the current head commit.
                 * 10.1. if the given name already exists, throw error "A branch with that name already exists."
                 * 10.2. create a new branch with the given name, and points it at the current head commit.
                 * */
                String branchName = args[1];
                branch = Branch.getBranch();
                if (branch.containBranch(branchName)) {
                    throw new GitletException("A branch with that name already exists.");
                }
                branch.newBranch(branch.getCurrentCommitId(), branchName);
                break;
            case "rm-branch":
                /** 11. "rm-branch": Deletes the branch with the given name.
                 * 11.1. if the given name not exists, throw error "A branch with that name does not exist."
                 * 11.2. if the given name same as the current branch, throw error "Cannot remove the current branch."
                 * 11.3. remove the branch of given name.
                 * */
                String branchNameToDelete = args[1];
                branch = Branch.getBranch();
                if (!branch.containBranch(branchNameToDelete)) {
                    throw new GitletException("A branch with that name does not exist.");
                }
                if (branch.getCurrentBranchName().equals(branchNameToDelete)) {
                    throw new GitletException("Cannot remove the current branch.");
                }
                branch.removeBranch(branchNameToDelete);
                break;
            case "reset":
                /** 12. "reset": Checks out all the files tracked by the given commit.
                 * 12.1. if the commit not exist, throw error "No commit with that id exists."
                 * 12.2. get all files' names in current working directory "CWD".
                 * 12.3. iterate all files, if the file is untracked in the current branch but in target commit, throw error "There is an untracked file in the way; delete it, or add and commit it first."
                 * 12.4. clear CWD
                 * 12.5. copy files in [commit id] to current working directory.
                 * 12.6. clear STAGE
                 * */
                branch = Branch.getBranch();
                String commitId = args[1];
                File commitFile = Utils.join(Commit.COMMITS, commitId);
                if (!commitFile.exists()) {
                    throw new GitletException("No commit with that id exists.");
                }
                Commit targetCommit = Commit.fromFile(commitFile);
                currentCommit = branch.getCurrentCommit();
                List<String> files = Utils.plainFilenamesIn(Repository.CWD);
                for (String fileNameInCWD : files) {
                    if (!currentCommit.isTracked(fileNameInCWD) && targetCommit.isTracked(fileNameInCWD)) {
                        throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                }
                for (String fileNameInCWD : files) {
                    File file = Utils.join(Repository.CWD, fileNameInCWD);
                    if (currentCommit.isTracked(fileNameInCWD) && !targetCommit.isTracked(fileNameInCWD)) {
                        file.delete();
                    }
                }
                Repository.clearStage();
                HashMap<String, String> filesInTargetCommit = targetCommit.getTrackedFiles();
                for (String fileTrackedInTargetCommit : filesInTargetCommit.keySet()) {
                    File source = Utils.join(Repository.BLOBS, filesInTargetCommit.get(fileTrackedInTargetCommit));
                    File target = Utils.join(Repository.CWD, fileTrackedInTargetCommit);
                    Repository.copyFile(source, target);
                }
                branch.forwardCurrentBranchTo(commitId);
                break;
            case "merge":
                List<String> filesListInStageAdd = Utils.plainFilenamesIn(Repository.CWD);
                filesInStageRemove = Repository.getRemovedFilesSet();
                if ((filesListInStageAdd == null || filesListInStageAdd.isEmpty()) && filesInStageRemove.isEmpty()) {
                    System.out.print("You have uncommitted changes.");
                    break;
                }
                branch = Branch.getBranch();
                if (!branch.containBranch(args[1])) {
                    System.out.print("A branch with that name does not exist.");
                    break;
                }
                if (args[1].equals(branch.currentBranchName())) {
                    System.out.print("Cannot merge a branch with itself.");
                    break;
                }
                currentCommitId = branch.getCurrentCommitId();
                String givenCommitId = branch.getBranchHeadCommitId(args[1]);
                String latestCommonAncestorCommitId = Commit.lastestCommonAncestorCommitId(currentCommitId, givenCommitId);
                if (latestCommonAncestorCommitId.equals(givenCommitId)) {
                    System.out.print("Given branch is an ancestor of the current branch.");
                    break;
                }
                Commit lastestCommonAncestorCommit = Commit.fromCommitId(latestCommonAncestorCommitId);
                currentCommit = Commit.fromCommitId(currentCommitId);
                Commit givenCommit = Commit.fromCommitId(givenCommitId);
                HashMap<String, String> trackedFilesInGivenCommit = givenCommit.getTrackedFiles();
                boolean unTrackedFileExist = false;
                for (String file: trackedFilesInGivenCommit.keySet()) {
                    File fileInCWD = Utils.join(Repository.CWD, file);
                    if (fileInCWD.exists() && !currentCommit.isTracked(file) && !Repository.sameWithBlob(fileInCWD, givenCommit.getBlobId(file))) {
                        System.out.print("There is an untracked file in the way; delete it, or add and commit it first.");
                        unTrackedFileExist = true;
                        break;
                    }
                }
                if (unTrackedFileExist) {
                    break;
                }
                if (latestCommonAncestorCommitId.equals(currentCommitId)) {
                    main(new String[]{"checkout", args[1]});
                    System.out.print("Current branch fast-forwarded.");
                    break;
                }
                boolean conflicted = false;
                for (String file: trackedFilesInGivenCommit.keySet()) {
                    File fileInCWD = Utils.join(Repository.CWD, file);
                    File fileInStagedAdd = Utils.join(Repository.STAGEADD, file);
                    String blobIdInCurrentCommit = currentCommit.getBlobId(file);
                    String blobIdInGivenCommit = givenCommit.getBlobId(file);
                    String blobIdInLCACommit = lastestCommonAncestorCommit.getBlobId(file);
                    if (blobIdInCurrentCommit.equals(blobIdInLCACommit) && !blobIdInGivenCommit.equals(blobIdInCurrentCommit)) {
                        fileInStagedAdd.createNewFile();
                        Repository.copyFile(Repository.getBlob(blobIdInGivenCommit), fileInCWD);  //checkout
                        Repository.copyFile(Repository.getBlob(blobIdInGivenCommit), fileInStagedAdd);  //stage add
                    } else if (!blobIdInCurrentCommit.isEmpty() && !blobIdInCurrentCommit.equals(blobIdInLCACommit) && blobIdInGivenCommit.equals(blobIdInLCACommit)) {
                        trackedFilesInGivenCommit.put(file, blobIdInCurrentCommit);
                    } else if (!blobIdInCurrentCommit.equals(blobIdInLCACommit) && !blobIdInCurrentCommit.equals(blobIdInGivenCommit) && !blobIdInGivenCommit.equals(blobIdInLCACommit)) {
                        // merge
                        Repository.mergeToFile(blobIdInCurrentCommit, blobIdInGivenCommit, fileInCWD);
                        // stage add
                        Repository.copyFile(fileInCWD, fileInStagedAdd);
                        conflicted = true;
                    } else if (blobIdInCurrentCommit.isEmpty() && blobIdInGivenCommit.equals(blobIdInLCACommit)) {
                        trackedFilesInGivenCommit.remove(file);
                    }
                }
                System.out.print("CheckPoint_0\n");
                for (String file: currentCommit.getTrackedFiles().keySet()) {
                    File fileInCWD = Utils.join(Repository.CWD, file);
                    String blobIdInCurrentCommit = currentCommit.getBlobId(file);
                    String blobIdInGivenCommit = givenCommit.getBlobId(file);
                    String blobIdInLCACommit = lastestCommonAncestorCommit.getBlobId(file);
                    if (blobIdInGivenCommit.isEmpty() && blobIdInCurrentCommit.equals(blobIdInLCACommit)) {
                        fileInCWD.delete();
                    }
                }
                System.out.print("CheckPoint_1\n");
                main(new String[] {"commit", String.format("Merged %s into %s.", args[1], branch.getCurrentBranchName()), givenCommitId});
                if (conflicted) {
                    System.out.print("Encountered a merge conflict.");
                }
                break;
            default:
                throw new IllegalArgumentException("No command with that name exists.");
        }
    }
    public static void validateArgs(String cmd, String[] args) {
        switch(cmd) {
            case "init":
                // TODO: handle the `init` command
                // initiallize the repository
                validateNumArgs("init", args, 1);
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                // Adds a copy of the file as it currently exists to the staging area.
                // only one file may be added at a time.
                validateNumArgs("add", args, 2);
                // TODO: fill the rest for "add" command
                isFile(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                // Saves a snapshot of tracked files in the current commit and staging area
                // so they can be restored at a later time, creating a new commit.
                if (args.length == 1) {
                    throw new GitletException("Please enter a commit message.");
                    }
                validateNumArgs("commit", args, 2, 3);
                break;
            case "rm":
                // Unstage the file if it is currently staged for addition.
                // If the file is tracked in the current commit,
                // stage it for removal and remove the file from
                // the working directory if the user has not already done so
                // (do not remove it unless it is tracked in the current commit).
                if (args.length == 1) {
                    throw new GitletException("Please enter the file name that you want to remove.");
                }
                validateNumArgs("rm", args, 2);
                // TODO: fill the rest for "rm" command
                isFile(args[1]);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                break;

            case "global-log":
                validateNumArgs("global-log", args, 1);
                break;
            case "find":
                validateNumArgs("find", args, 2);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                break;
            case "checkout":
                validateNumArgs("checkout", args, 2, 4);
                if (args.length >= 3) {
                    isFile(args[args.length - 1]);
                }
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                break;
            default:
                throw new GitletException("Command not found.");
        }
    }
    public static void validateNumArgs(String cmd, String[] args, int lo, int hi) {
        if (args.length < lo || args.length > hi ) {
            throw new RuntimeException(
                    String.format("Incorrect operands for: %s.", cmd));
        }
    }
    public static void validateNumArgs(String cmd, String[] args, int lo) {
        validateNumArgs(cmd, args, lo, lo);
    }
    public static void isFile(String fileName) {
        if (!fileName.contains(".")) {
            throw new GitletException(String.format("%s is not a file", fileName));
        }
    }

}
