# git-revisionist-historian

Manages updates to example/instructional repositories which are built up from multiple incremental changes, and
automatically associates tags and/or branches with them based on unique strings in the commit message.

For example, when **`git rh`** is run against the **`solution`** branch of an instructional repo, a commit which
introduces the fifth consecutive feature or piece of functionality would have the following automatically performed:
* The **prior commit (HEAD^)**, having a commit message of **"feature4"**, would be tagged **"feature5-start"**
* The commit itself, with a commit message of **"feature5"** would be **tagged "feature5-finish"**, and also
  have a **"feature5" branch** pointed at it.

So, imagine you needed to **update a feature introduced in an earlier commit**, for example in **"feature2"**.  You can
use `git rebase --interactive` to change it, and have all the subsequent commits/features rebased on top of it, then
`git push --force-with-lease`.

**BUT** this now means all the old **"...-start"** and **"...-finish"** tags and branches are invalid,
pointing to old orphaned SHAs that are no longer on the `solution` branch.

This problem is solved by running **`git rh`** - it will **automatically recreate all the specified tags and branches
you need based on the commit messages**, and **automatically do all the necessary `--force-with-lease` git pushes**
to the specified git remote server. 

NOTE: Both tags and branches are supported, because the git support in various tools/IDEs may make it easier
for students working with the example/instructional repo to compare tags vs. branches.  Specifically, in
IntelliJ Idea, there is a keymap for single-command "Compare with Branch...", but compare a local state with a 
tag requires going through multiple steps.

# Implementation Overview

## CLI

* Executable CLI tool: `git-rh` - will act as `git rh` extension when added to path.
* Usage (run from writable clone of repo which will be revised):
  * `git rh [--dry-run|-n] [--no-push] --config|-c grh-config.json` - When run within a checkout of a git repo, performs the revisions session,
    and automatically forces the update and push of all created/modified tags and branches.
* `--config|-c` can point to a config file via a relative path in the repository, an external path, or at an HTTP URL.  Default location is `./grh-config.json`.
* `--dry-run|-n` will print out progress but not actually make any changes, neither local nor remote.
* `--no-push` will make all changes to the local clone of the repo, but not push them to the origin.  Note that since
  all local changes are essentially idempotent because they are forced, this option can be used to review changes
  locally without pushing them, then run again without this option to push them. 
* `git rh` will fail and refuse to run if:
  * You are not in a git repo
  * There are any uncommitted changes in the working copy.
  * Any of the `branches` entries are the same as the `branchToRevise`
* `git rh` will also fail **after making local changes** if it does not have permissions to force push to the remote.

## Config File

Config file format

* `remote`: optional, string, defaults to `origin`
* `branchToRevise`: required, string
* `incrementCommits`: required, array of `incrementCommit` objects, which consist of:
  * `message`: required, regular expression
  * `tags`: optional, array of strings
  * `branches`: optional, array of strings

```json
{
  "remote": "origin",
  "branchToRevise": "solution",
  "incrementCommits": [
    {
      "message": "initial commit",
      "tags": ["feature1-start"],
    },
    {
      "message": "first feature",
      "tags": ["feature1-finish","feature2-start"],
      "branches": ["feature1"] 
    },
    {
      "message": "second feature",
      "tags": ["feature2-finish", "feature3-start"],
      "branches": ["feature2"] 
    },
    {
      "message": "third feature",
      "tags": ["feature3-finish"],
      "branches": ["feature3"] 
    }
  ]
}
```

## Workflow

1. `git rh --config path/to/grh-config.json` - begin process, and create internal state-tracking
   file at `~/.grh-state.json`
1. Perform pre-run validations, fail if any do not pass
1. Fetch specified remote to get latest state.
1. Consecutively process all incrementCommits by
    1. Applying specified tags (with `--force` in case they already exist)
    1. Applying specified branches (with `reset --hard` if they already exist)
1. Continue until all incrementCommits are successfully processed.
1. Push (`--force-with-lease` for branches) all modified tags and branches.

# Building and Running

* `./gradlew clean build jar`
* `java -jar build/libs/git-revisionist-historian.jar`