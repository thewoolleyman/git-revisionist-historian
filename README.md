[Pivotal Tracker Project](https://www.pivotaltracker.com/n/projects/2092368)

# IMPORTANT ALPHA NOTES

This project is a work in progress.  See the [Pivotal Tracker Project](https://www.pivotaltracker.com/n/projects/2092368)
for current status.  Note the following known issues:

* **Versioning/Publishing approach is being worked out** - for now obtain the latest dev jar via
  `curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/dev/git-revisionist-historian-dev.jar`
* **There is not yet a native git extension executable "`git-rh`" (invokable as `git rh` when it
  is on the path), so for now assume all occurrences of `git rh` in the docs to be replaced with
  `java -jar path/to/git-revisionist-historian.jar [options...]`**.  See "Building and Running Locally" below and
  watch [this story](https://www.pivotaltracker.com/story/show/150307700)
  and [this story](https://www.pivotaltracker.com/story/show/150603755) for details.

# git-revisionist-historian

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

* An executable CLI tool: `git-rh` - will act as `git rh` extension when added to path.
* Usage (run from a writable clone of a git repo to be be revised):
  * `git rh [--dry-run|-n] [--skip-push|-s] --config|-c grh-config.json` - When run within a checkout of a git repo,
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
* `git rh` will fail and refuse to run if:
  * You are not in a git repo
  * Any other option or config file validation errors occur.
* `git rh` will also fail **after making local changes** if it does not have permissions to force push to the remote.

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
  * `git rh --processor cli [--config path/to/grh-config.json] [other options...]`

* For `--processor api`:
  * Export the GITHUB_PERSONAL_ACCESS_TOKEN env var to a
  [Github Personal Access Token](https://github.com/settings/tokens)
  * `git rh --processor api --repo "owner/name" [--config path/to/grh-config.json] [other options...]`


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

1. `git rh --config path/to/grh-config.json` - begin revision process.
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

* Download the [latest built releases](https://concourse.pal.pivotal.io/teams/main/pipelines/grh):
  * dev: **curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/dev/git-revisionist-historian-dev.jar**
  * alpha: **curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/x.y.z-rc.n/git-revisionist-historian-x.y.z-rc.n.jar**
  * final: **curl -O https://s3.amazonaws.com/git-revisionist-historian/com/thewoolleyweb/grh/git-revisionist-historian/x.y.z/git-revisionist-historian-x.y.z.jar**
* (Coming soon): Install via Maven/Gradle (TODO: document)
* (Coming soon*): Native executable
* (Coming soon*): Concourse resource
* (Coming soon*): Install via Homebrew
* (Coming soon*): Install via rpm/dpkg

(* Maybe never...)
