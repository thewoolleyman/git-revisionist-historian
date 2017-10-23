[Pivotal Tracker Project](https://www.pivotaltracker.com/n/projects/2092368)

# git-revisionist-historian

# TL;DR

* Automatically re-applies tags to rebased branches, based on commit messages.
* It is run as an executable CLI tool - currently only as an executable jar,
  but [could be published as a native binary](https://www.pivotaltracker.com/story/show/150307700))
* It has two "processer" modes:
  * `cli`, which uses the git CLI and can be manually run in a local git repo
  * `api`, which uses the github API and can be run on CI (even Concourse, where you don't have
    write permission on the build job instance's git clone)
* This project is a work in progress.  See the [Pivotal Tracker Project](https://www.pivotaltracker.com/n/projects/2092368)
  for current status.

# What it does

Manages updates to tags of example/instructional repositories which are built up from multiple incremental
changes, and automatically applies tags to them based on unique strings in the commit message.

For example, when **`git rh`** is run against the **`solution`** branch of an instructional repo, a commit which
introduces the fifth consecutive feature or piece of functionality would have the following automatically performed:
* The **prior commit (HEAD^)**, having a commit message of **"feature4"**, would be tagged **"feature5-start"**
* The commit itself, with a commit message of **"feature5"** would be **tagged "feature5-finish"**.

So, imagine you needed to **update a feature introduced in an earlier commit**, for example in **"feature2"**.  You can
use `git rebase --interactive` to edit it, and rebase all of the subsequent commits/features on top of it, then
`git push --force-with-lease`.

**BUT**, since rebasing changes commit SHAs, this now means all the old **"...-start"** and **"...-finish"** tags are invalid,
pointing to old orphaned SHAs that are no longer on the `solution` branch.

This problem is solved by running **`git rh`** - it will **automatically recreate all the specified tags
you need, pointing them to the correct commits based on their messages**, and **automatically do all the necessary `--force-with-lease` git pushes**
to the specified git remote server.

~~NOTE: Both tag and branch re-creation is supported, because the git support in various tools/IDEs may make it easier
for students working with the example/instructional repo to compare tags vs. branches.  Specifically, in
IntelliJ Idea, there is a keymap for single-command "Compare with Branch...", but compare a local state with a 
tag requires going through multiple steps.~~(actually not yet, see [this story](https://www.pivotaltracker.com/story/show/150286123) - but you can always make a branch pointing to a "finish" tag)

# Implementation Overview

## Command Line Tool

***(TODO: rewrite this to a more standard and concise manpage-like syntax format)***

* An executable CLI tool: `java -jar path/to/the.jar`.  Could eventually be published as a `git-rh` native executable which
  would act as `java -jar path/to/the.jar` extension when added to path.
* Usage (run from a writable clone of a git repo to be be revised):
  * `java -jar path/to/the.jar [--dry-run|-n] [--skip-push|-s] --config|-c grh-config.json` - When run within a checkout of a git repo,
    performs the revision process,
    and automatically forces the update and push of all created/modified tags.
* `--config|-c CONFIG` can point to a config file via a relative path in the repository, an external path, or at
  an HTTP URL.  Default value is `./grh-config.json`.
* `--processor|-p PROCESSOR` can be `cli` or `api`.  If `cli`, all changes will be pushed and applied via the git
  command line in the current working directory.  If `api`, all changes will be applied via the Github API.
  * `GITHUB_PERSONAL_ACCESS_TOKEN` Environment variable which specifies the
    Github Oauth [Personal Access Token](https://github.com/settings/tokens)
    to use.  Required when `--processor=api`.
  * `--repo|-r REPO` specifies the github repository name to use.  Required and only valid when `--processor=api`.
  * `--remote|-o REMOTE` specifies the name of the remote repository in the local git config.  Required and only
    valid when `--processor=cli`.
  * `--skip-push|-s` will make all changes to the local clone of the repo, but not push them to the origin.
    Note that since all local changes are essentially idempotent because they are forced, this option can be
    used to review changed tags locally without pushing them, then run again without this option
    to push them.  Only valid when `processor=cli`.
* `--dry-run|-n` will print out progress but not actually make any changes, neither local nor remote.
* `java -jar path/to/the.jar` will fail and refuse to run if:
  * You are not in a git repo
  * Any other option or config file validation errors occur.
* `java -jar path/to/the.jar` will also fail **after making local changes** if it does not have permissions to force push to the remote.

## Usage

### First, update the repo with your revisions and push

1. Make revisions to the `branchToRevise` and push
  1. `git checkout <branchToRevise>`
  1. `git pull`
  1. `git rebase -i --root` (see git rebase manpage for details, the following steps are a summary)
  1. Change "pick" to "edit" for the commits you wish to revise
  1. Rebase will stop on those commits, make the revisions, then `git add/delete` and `git commit`
  1. `git rebase --continue` until complete, resolving any conflicts and performing remaining edits
  1. Run any necessary tests, etc to verify revisions.
  1. `git push --force-with-lease` the rebased `branchToRevise`

### Then, run git-revisionist-historian to update the tags specified in the config file 

* For `--processor cli`:
  * `java -jar path/to/the.jar --processor cli [--config path/to/grh-config.json] [other options...]`

* For `--processor api`:
  * Export the GITHUB_PERSONAL_ACCESS_TOKEN env var to a
  [Github Personal Access Token](https://github.com/settings/tokens)
  * `java -jar path/to/the.jar --processor api --repo "owner/name" [--config path/to/grh-config.json] [other options...]`


## Config File

Config file format

* NOTE: The commits in the config file are listed in reverse chronological order by convention,
  to match the way `git log` and the Github API work.  But, it should not matter what order they
  are listed in (once [this story](https://www.pivotaltracker.com/story/show/150258665) is done).
* `branchToRevise`: required, string
* `incrementCommits`: required, array of `incrementCommit` objects, which consist of:
  * `message`: required, *case insensitive* regular expression (via Kotlin `message.toRegex(RegexOption.IGNORE_CASE)`)
  * `tags`: required, array of strings (may be empty)

```JSON
{
  "branchToRevise": "solution",
  "incrementCommits": [
    {
      "message": "in-progress feature",
      "tags": []
    },
    {
      "message": "third feature",
      "tags": ["feature3-finish"]
    },
    {
      "message": "second feature",
      "tags": ["feature2-finish", "feature3-start"]
    },
    {
      "message": "first feature",
      "tags": ["feature1-finish","feature2-start"]
    },
    {
      "message": "initial commit",
      "tags": ["feature1-start"]
    }
  ]
}
```

## What It Does

1. `java -jar path/to/the.jar --config path/to/grh-config.json` - begin revision process.
1. Perform pre-run validations, fail if any do not pass.
1. Fetch specified git remote to get latest state.
1. Consecutively process all `incrementCommits` by applying specified
   tags (with `--force` in case they already exist)
1. Continue until all `incrementCommits` are successfully processed.
1. Push (with `--force --tags`) all modified tags.

# Building and Running Locally

## Build

* `./gradlew clean build jar`

## Run

* `java -jar build/libs/git-revisionist-historian.jar [options...]`

## Test

* `./gradlew test` - unit tests
* `./gradlew acceptanceTest` - acceptance tests - requires credentials to be set

# Downloading

* Download the executable jar [latest built releases](https://concourse.pal.pivotal.io/teams/main/pipelines/grh):
  * dev: **curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/dev/git-revisionist-historian-dev.jar**
  * alpha: **curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/x.y.z-rc.n/git-revisionist-historian-x.y.z-rc.n.jar**
  * final: **curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/x.y.z/git-revisionist-historian-x.y.z.jar**
* Install via Maven/Gradle: It's published to S3 as a library which can be used directly from
  Maven or Gradle (TODO: document)
* (Coming soon*): Native executable
* (Coming soon*): Concourse resource
* (Coming soon*): Install via Homebrew
* (Coming soon*): Install via rpm/dpkg

(* Maybe never...)

# Note about how tags are viral in git

Git tags (AKA tag refs), unlike branches (AKA head refs) are viral - because
they are all pulled/pushed at once by `git fetch/push --tags`,
rather than just one at a time like branches.

They are also ignored by default by `git fetch --prune`, unlike branches.

That means any deleted tags will "show back up" as people fetch/push with the `--tags`,
EVEN if they are deleted from the remote.

To get around this, you can either re-clone the repo, or have everyone always use the
following command when fetching tags:

`git fetch --prune origin "+refs/tags/*:refs/tags/*"`
