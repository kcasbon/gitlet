# Gitlet Design Document
Author: Kyle Casbon

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

### Main.java

The driver class for Gitlet. This class will handle any acceptable
command line inputs and throw an exception if it receives an invalid
input. This class ***will not actually handle any of the commands'
function***. It should only make calls to methods in other classes.

#### Fields

  * **static final Boolean _isRepo:** should be set to true if a repository has been
  instantiated. If true, calling init in the command line should throw an exception
  * **static final File CWD:** refers to the current working directory

### Commit.java

The commit object that will contain some metadata (namely the 
date/timestamp of the commit, the SHA-1 ID of the commit, references
to the relevant tracked blobs, and the message describing the purpose
of the commit). 

#### Fields

  * **private static final String _ID:** this instance variable should hold the SHA-1 
  ID of the commit object for reference
  * **private static final Date _date:** this should hold the date/time that the 
  commit was made at (in PST)
  * **private static final String _message:** this will hold the commit message provided 
  by the client
  * **private static ArrayList(String) _trackedBlobs:** this ArrayList will hold the
  SHA-1 IDs of the Blob objects that are relevant to the current commit
  * **private static String _parent:** contains the SHA-1 ID of the "parent" Commit 
  object
  * **private static String _secondParent:** should be null by default (should only have
  two parents in the case of a merge)

### Blob.java 

A blob should contain the information/contents of a file that the client 
wishes to track. Each commit object (except the initial commit) should 
the SHA-1 IDs of the blobs whose files they wish to track. Each blob's 
contents can then be used to rewrite information from previous commits in
order to return the repository to a previous state. 

#### Fields

  * **private static final String _ID:** this instance variable should hold the SHA-1
    ID of the commit object for reference
  * **private static final File _contents:** this File should contain the contents that
  the client wishes to be committed and should persist

### Repository.java

Will contain the staging area as well as the staged for removal area and 
handle the add and rm functionalities. The instantiation of this class
will reflect the creation of a Gitlet repository. Only one of these objects
should exist.

#### Fields
  * **private static ArrayList(Blob) _additionStage:** An ArrayList containing every Blob 
  that should be accessible to the next commit object to be instantiated.
  * **private static ArrayList(Blob) _removalStage:** An ArrayList containing every Blob 
  that will be removed from the next commit.
 
## 2. Algorithms

### Main.java

#### Methods

* **main(String... args):** This main method should first make sure there are > 0 args 
  passed in on the command line. Then, using switch/case functionality, this will "read"
  the first argument passed into the command line and choose a course of action. If the
  command passed in is not valid (supported by the program), an exception should be thrown.

### Commit.java

#### Methods

* **Commit(String message):** This constructor should set the date and message metadata,
  add relevant blobs to _trackedBlobs, set correct parent commit, and then, once this 
  is all done, calculate the unique SHA-1 ID that will refer to the Commit object.
* **getID(String _ID):** This is a getter method for the Commit SHA-1 ID.
* **getDate(Date _date):** This is a getter method for the Commit date.
* **getMessage(String _message):** This is a getter method for the Commit message.
* **getParent(String parent):** This is a getter method for Commit parent.
* **getParent2(String parent2):** This is a getter method for second Commit parent (if applicable).
* **findBlob(String id):** This finder method returns the blob in _trackedBlobs with the given 
  SHA-1 ID. If this ID does not refer to any Blob in the array, throw an exception.
* **commitFile(File file):** This method should call the Blob constructor on a given File
  and add its SHA-1 ID to _trackedBlobs. This will allow the Commit to keep track of all the
  relevant files. If the File doesn't exist, throw an exception.

### Blob.java

#### Methods

* **Blob(File file):** Constructor for a blob object. Will be called when
  a file is added/staged for addition. This constructor does not store any metadata,
  This blob should also be added to Repository._additionStage so that it is included in
  the next commit.
* **getID(String id):** This is a getter method for the Blob's SHA-1 ID.

### Repository.java

#### Methods

* **Repository(File cwd):** This constructor creates a repository object in the CWD (there should
  be only one of these in any given directory.

## 3. Persistence

### add [file]

When add is called on the command line, a Blob should be instantiated which will write the
contents of the given File into a hidden file and add the file to _additionStage. This way
the files that need to be added persist.

### commit [message]

Similar to the above, the files persist because the Blobs containing the file contents are 
stored in the .gitlet hidden folder.


## 4. Design Diagram

![](../../../Desktop/DesignDiagram-1.png)

