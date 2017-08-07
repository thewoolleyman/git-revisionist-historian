# NOTE - NOT IMPLEMENTED

This `README-branch-based-approach.md` contains a design for a branch-based approach which was more flexible:

* It supports more than one commit per increment.
* It does not rely on matching specific commit messages strings to identify an increment.
* It did not require manually doing an interactive rebase and force-push of the branch.
* It only requires a single command which does not rely on current state.

...but it was also more complex:

* It is a multi-branch process.
* It is a multi-step process.
* It requires the same number of conflict resolutions as a manual interactive rebase, but
  spread across multiple separate rebases of successive branches, which is potentially more confusing.
* It requires multiple commands and knowledge of state.

So, the initial implementation went with the less flexible, but simpler, approach.    

# git-revisionist-historian

Manages updates to example/instructional repositories which are built up from multiple incremental changes.

An existing main "***branchToRevise***" is built up from changes which consists of multiple "***incrementBranches***"
being consecutively merged in.

Each consecutive branch merge will be tagged with "<incrementBranch name>-before" and "<incrementBranch name>-after" tags.

If any of the ***incrementBranches*** needs to be updated, it can be checked out and modified as needed, either by
adding new commits to the branch, or by using `commit --amend` or `rebase --interactive` to change existing commits
to the branch, ***but not merging it back into the branchToRevise***.

Instead of manually merging your changes, run the **`git rh begin --config <.../grh-config.json>`**
command.  This will rebuild a ***temporary version*** (with a **`grh-tmp-`** prefix)
of your ***branchToRevise*** from scratch, by ***automatically rebasing and merging in*** all of the consecutive
incrementBranches, ***including any you have modified,***, and re-creating the
"-before" and "-after" tags.

If there are any rebase conflicts while processing the subsequent incrementBranches, `git rh` will pause,
and give you the opportunity to resolve the conflicts.  When you have finished and staged the conflict resolution(s),
run **`git rh continue`** to continue processing the remaining commits on the current and subsequent incrementBranches.

Once all the ***incrementBranches*** have been applied to the temporary version of the ***branchToRevise***,
`git rh` will pause, and give you the opportunity to review the new commit history of ***branchToRevise***,
run any tests, etc.  There will be temporary versions of all the incrementBranches as well as all "-before" and "-after"
tags.  Once you are satisfied the revisions are good, run **`git rh finalize`**, which will `reset --hard` all the
current branches/tags with the new temporary ones, `git push --force-with-lease` all the new ones to the remote repo,
then clean up all the temporary ones, leaving you in a clean state for the next time you need to `git rh start`.


# Overview

## CLI

* Executable CLI tool: `git-rh` - will act as `git rh` extension when added to path.
* Usage (run from writable clone of repo which will be revised):
  * `git rh begin --config grh-config.json` - Begin a new revision session (fails if one is already in progress).
    `--config` can point to local path or http URL. 
  * `git rh continue` - to continue after a pause to resolve a rebase conflict when updating an incrementBranch
  * `git rh finalize` - to apply and push the new state of all branches/tags to the remote repo.  
  * `git rh reset` - to delete any existing in-progress state and/or temporary branches.

## Config File

* Config file format (`remote` is optional, defaults to `origin`):
  ```json
  {
    "remote": "origin",
    "branchToRevise": "master",
    "incrementBranches": [
      "initial-commit",
      "more-changes",
      "latest-changes"
    ]
  }
  ```

## Workflow

1. `git rh begin --config path/to/grh-config.json` - begin process, and create internal state-tracking
   file at `~/.grh-state.json`
    * Fail with an instruction to run `git rh reset` if any `grh-tmp-*` branches or state file already exists.
1. Create new temp branch `grh-tmp-<branchToRevise>`
1. Consecutively apply all incrementBranches by
    1. Applying tag `grh-tmp-<incrementBranchName>-before`
    1. Checking out a temp branch at `grh-tmp-<incrementBranchName>`
    1. Rebasing it onto `grh-tmp-<branchToRevise>` with:
      `git rebase --onto grh-tmp-<branchToRevise> <branchToRevise> grh-tmp-<incrementBranchName>`
    1. Merging it into `grh-tmp-<branchToRevise>`
    1. Applying tag `grh-tmp-<incrementBranchName>-after`
1. If any conflicts are encountered when rebasing, record the current branch in the state file, then pause and allow
   user to resolve the conflicts, stage the changes, then run `git rh continue`
1. Continue until all incrementBranches are successfully rebased and merged.
1. Pause to let user review/test all the temporary state.
1. When user runs `git rh finalize`:
    1. `git reset --hard` `branchToRevise` and all `incrementBranches` to the corresponding `grh-tmp-*` branch's
      revisions.
    1. `git tag --force` all existing `*-before` and `*-after` tags to the corresponding `grh-tmp-*` revisions.
    1. `git push --force-with-lease` `branchToRevise` and all `incrementBranches` to the specified remote.
    1. `git push tags` to the specified remote. 
    1. Delete all `grh-tmp` branches and `~/.grh-state.json` state-tracking file. 