
Why git??
=========

Brief History
-------------

Git became the most famous version control system(VCS) after Linus Torvalds first developed it. Lots of developers, companies, and organizations are using git while developing a program, and it is now prevalent to make public the program by uploading it to a public git remote repository.

While Linus was implementing the Linux kernel, he felt that the procedure of developing the program would become much more comfortable if there exists proper VCS. Even though there were some commercial VCS, like SVN or Bitkeeper, he thought that they have various demerits. Therefore, he decided to make new, outstanding VCS, and the result is \"git.\"

git, github, and gitlab
-----------------------

Many novice developers, or undergraduates, don't know the difference between **git** and **Github**. It is quite simple. \"Git\" denotes the \"program\" or \"system,\" which services such functionalities: version control, managing source code, ease collaborating, maintaining repository\...etc. Git can be installed and used in almost all of the major OSs, such as Linux, macOS, or Windows,and also can be used locally.

However, using git locally, which means that maintaining your source code and git system without accessing the internet, quite exacerbates the potential power of it. We will soon see that git is a potent tool for backup, but only with the maintenance of the remote repository. So, think about the situation: you merely maintain your git repository and source code on your laptop, and suddenly the computer goes wrong due to some external reason. If you fail to recover datum from your disk drive, there might be no more ways to restore source code. But if there is a server that you uploaded your source code and corresponding git configurations, you can easily restore data from the server, by just cloning it. Services like \"Github\" or \"Gitlab\" takes the role. They provide a remote repository that users can upload their projects so that users are now available to maintain the source code safely and access to it in various places easily.

### Github

For just fun, let's talk about Github more precisely, as it the most popular remote git repository service among developers. Git and Github used for the majority of existing projects, due to its stability and usability. Also, as using Github for open source project is free, it became the Macca of open source. For example, the entire source code of Linux or Google's ML library Tenserflow is opened to the public on Github.

<center><img src="https://jeonhyun97.github.io/images/linux_github.png" width="450" ></center>  

*<center>Linux source code opened in Github. Linux Torvalds is still controlling the entire commit history</center>*

The two most potent functionality provided by Github is *Fork* and *Pull request*. Anyone can fork the existing repository opened in Github and work on it. If you think that your work has worth to be merged in an open-source project, you can send *Pull request* about your work in the forked repository. Then the owner of the project will check your work and determine whether your contribution will be merged into the project or not.

Pros of git
-----------

Learning git is not that easy. Git generally generates overhead, and misusing it sometimes ruins the entire project. Nevertheless, we should use git due to its outstanding advantage:

1.  Powerful version control

2.  Backup

3.  Collaboration

From now on, we will see how git achieves these three advantages and their details. Understanding it is quite essential for precise usage of git and helps a lot while developing something, especially with other people.

How advantages are achieved
===========================

Version Control
---------------

Without a version control system, we always need to save our entire file history, naming each of them differently. If so, your working directory will be messed up by various files named like: `code_draft.c, code_2.c, code_3.c, code_branch_1.c, code_branch_2.c, code_reverted.c`\...

The method is quite inefficient in two different perspectives. First of all, it is hard to manage history. Consider the situation: you recognized that your code became impossible to be revived, and decided to go back to the previous version. Now, there comes out various issues. How can you find a specific version among unstructured, messy files? What will be the proper name of the files? Does the reverted file should be deleted or not? You need to select appropriate manners for each issue and should apply it manually, and this generates enormous overhead.

The second reason is that the method consumes vast storage. If the size of your files is only 2 or 3 megabytes, it's okay. However, what if your project is massive, and therefore needs gigabytes of storage to save? Now the problem occurs. If you keep all the copies of each version, the entire system might occupy more than a hundred gigabytes.

Git solves these two problems efficiently. It automatically saves and maintains your code history, and provides the way to access to a certain point of the past or revert the changes. Also, git only collects the information about the \"difference\" between each commit that you made, so that it can maintain full history with efficient storage usage.

Then how can we save and manage history? It's easy. You might be already familiar using the command `git add` and `git commit`. Then why we need these two separate commands? It seems that using two commands generates unnecessary waste of time. However, `git add` command provides a significant advantage in which gives the user the authority to select *the range of commit*. For example, while you are implementing something, your high concentration might make you forget to commit frequently. You already worked in more than ten files. In this case, is it reasonable to commit all these works at once? The answer is **NO**. It is better to assign each small task to individual commitments, as it helps you manage the commit history. Now you might notice why you need `git add` command. In this situation, you can successively commit the change of each file by using the sequence of commands: `git add file1` -> `git commit` -> `git add file2` -> `git commit` -> `git add file2` \... and so on.

Backup
------

We already talk about the backup system provided by git. To be more specific, we must know what **repository** is. Git repository is quite similar to ordinary \"folder\" or \"directory.\" It contains child directories and files as usual directories do. The main difference between directory and git repository is that a repository contains a unique child repository named `.git/`. `.git` is a hidden directory in which contains git configuration and significant infos: commit history, remote repository address, SSH key for secure, etc.

### Repository

Any existing folder(or directory) in your computer can be easily converted to a git repository using command git init. This command adds git repository configuration to the directory by creating a .git folder. Now we can call the directory *local git repository*, and can use any git command on it.

However, you need to prepare against emergencies, such as the sudden broken of your computer. To keep your files in local git repository safe, you need to upload them somewhere. Sevices like Github or Gitlab provides *remote git repository*  for the purpose. We can easily upload everything in your local git repository to remote git repository with this simple procedure:

1.  Create an empty remote repository in Github(or Gitlab). Suppose that the URL of the remote repository is `https://github.com/testproject.git`.

2.  Add the remote repository to your local repository using the command: `git remote add origin https://github.com/testproject.git`. The command orders to add new git remote repository to local repository, while naming it as `origin`.

3.  Send everything in your local repository to a remote repository using the command: `git push origin master`. This orders to push (send) everything in your *master branch* (main working set) to the remote repository `origin`. After the first `push` command, you can just type `git push` for the same functionality.

And if you want to download the change of the remote repository (maybe made by someone else) to the local repository, this can be achieved by the command: `git pull`. Sometimes you might want to create a new git local repository, probably at the other computer. In this case, `git clone` `https://github.com/testproject.git` command will help you to create a new local repository containing every info in the remote repository `https://github.com/testproject.git`.

### Distributed Version Control System

DVCS gives the user huge advantages comparing to the Centralized version control system (CVCS). *Wikipedia* may be the proper example to talk about CVCS. Everyone can access Wikipedia and also able to edit it, but only on the web. Also, no local repositories are allowed. Therefore we cannot access the information and edit the history of Wikipedia if we cannot access the internet. Maybe now you know that git has all these functionalities, due to the reason that it is DVCS.


<center><img src="https://jeonhyun97.github.io/images/linux_github.png" width="450" ></center>  

*<center>We can edit Wikipedia by only accessing it through the internet.</center>*

Collaboration
-------------

Actually, functions for collaborating is highly related to those for the backup system. Think about such circumstances around the project: 3 developers **Alice**, **Bob**, and **Charlie** are participating in the project, and only Alice has the initial git repository of the source code. In this situation, Alice can share codes with Bob and Charlie by creating a remote repository in Github and sending the URL to them. Then Bob and Charlie can easily clone the files in the remote repository. After the procedure, three collaborators will have the same copy in their local repositories.

After the initial process, developers must develop something. The procedure of \"developing\" with the project will be like this:

1.  Alice writes something, `commit`s the change, and `push`es it to the remote repository.

2.  Bob `pull`s out the latest work from the remote repository and again performs the write-commit-push procedure.

3.  Charlie `pull`s out the latest work from the remote repository and again performs the write-commit-push procedure.

If these three procedures execute sequentially, it's okay. Bob writes on Alice's work, and Charlie writes on Bob's work. There is nothing complicated. However, what if the second and the third procedure executes concurrently? Consider the situation that Bob and Charlie both `pull` out the work of Alice and work on it. What will happen when they `push` their work? If Charlie and Bob worked on different files, it's okay. The git will automatically merge their work safely. But when they work on the same file, a complicated situation occurs.

### Branch

Before answering the above question, you must understand the git branch summarily. You can make a branch anytime you want by using `git branch` command, and this will provide you free working set separated from the main(master) branch. If the developer wants to implement new functionality which can harm the entire project, it is reasonable to make a branch and work on it. As it is completely segregated from the main branch, the developer doesn't have to worry about other's job and probable conflicts which can occur while developing the function. And after he or she finishes implementing the feature and ends debugging/testing, he can simply merge his work into the main branch (by `git merge` command).

A branch is a potent tool for various situations: making new functionality, testing for an experimental version of the project, or just want to have a working area separated from other collaborators. However, we have to always keep in mind that the longer a branch is, the harder to merge it to the main(master) branch. Let's assume that there are two branches ready to be merged. If developers of two branches worked for the different parts of the project, it's okay. Git will safely merge it. However, if two branches modified the same area, which modification should be selected? Can git merge them automatically, or the developer should combine it manually?

### 3-way merge

A simple example will help you understand the concept. Consider there are two branches **A** and **B**, and they branched out from the specific point of the commit history **Base**. Also, assume that git has only a single file `test.txt` with four rows, and **A**, **B**, and **Base** each contains:



 | | A | Base | B| 
|---|:---:|---:|---:|
| `row1` | a | 1 | 1 |
| `row2` | 2 | 2 | 2 |
| `row3` | a | 3 | b |
| `row4` | 4 | 4 | b |

If we merge two branches **A** and **B**, what will happen? Git provides a polished method for the procedure. The rule is simple:

1.  If **A** changed comparing to **Base** and **B** didn't, select **A**'s commitment and vice versa.

2.  If both didn't change, maintain the original.

3.  If both changed, impossible to merge automatically. Git asks the developer to merge it manually.

So, let's apply these rules to the situation. In the first row, only **A** changed the value (from 1 to a), but **B** didn't. Therefore, adopting the first rule, we can quickly know that the merged value will be the value of **A**. This rule can be applied to `row 4`. It is evident that the merged result will be the value of **B**.

Then how about `row 2` and `3`? For `row 2`, no one will doubt that the result will be 2. Rule 2 can be applied to the case. However, for row 3, it is impossible to use both rule 1 and rule 2 and, therefore, should apply rule 3. Git cannot merge automatically for law 3 and will ask the developer to merge it. The final result will be like this:

 | | A | Base | B| 3-way merge |
|---|:---:|---:|---:|---:|
| `row1` | a | 1 | 1 | a |
| `row2` | 2 | 2 | 2 | 2 |
| `row3` | a | 3 | b | ?? |
| `row4` | 4 | 4 | b | b |

The algorithm for the 3-way merge is naive but gives a huge advantage. The core of the procedure is that it also uses the **Base** node to compare. Suppose that we only compare the values from branches **A** and **B**? Git will not be able to select amount automatically and ask a human to merge for not only row 3 but also for rows 1 and 2, as they have a different value. Now you may agree that the 3-way merge improves the efficiency of merging step a lot.

Now you may understand what will happen when two branches merged. The same thing happens for the example for Bob, Alice, and Charlie. Alice's work will be treated as **Base**, and the branches **A** and **B** will represent the work or Bob and Charlie. 

Useful Commands
===============

In this section, I'll introduce the set of useful git commands which can help you a lot in a group project. It will be sufficient to learn only these commands for a successful project. Reference [link]( https://confluence.atlassian.com/bitbucketserver/basic-git-commands-776639767.html) for the detailed explanation of each command.

-   `git init`

-   `git remote`

-   `git status`

-   `git add`

-   `git commit`

-   `git revert`

-   `git reset`

-   `git pull`

-   `git push`

-   `git checkout`

-   `git log`

-   `git log –graph –oneline`


