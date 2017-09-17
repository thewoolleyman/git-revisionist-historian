package test.unit.grh.config

import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.config.IncrementCommit
import com.thewoolleyweb.grh.git.Command
import com.thewoolleyweb.grh.git.Commit
import com.thewoolleyweb.grh.git.Log
import com.thewoolleyweb.grh.plan.Action
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.plan.createPlan
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class createPlanTest : StringSpec() {
  init {
    val config = GrhConfig(
      branchToRevise = "solution",
      incrementCommits = listOf(
        IncrementCommit(
          message = "initial commit", // Note that this will be an IGNORE_CASE regex match
          tags = listOf("feature1-start"),
          branches = listOf("feature1")
        ),
        IncrementCommit(
          message = "first feature",
          tags = listOf("feature1-finish", "feature2-start"),
          branches = listOf("feature1", "another-branch")
        ),
        IncrementCommit(
          message = "second feature",
          tags = listOf("feature2-finish"),
          branches = listOf("feature2")
        )
      )
    )

    val log = Log(
      commits = listOf(
        Commit(
          sha = "c1",
          message = "Initial commit"
        ),
        Commit(
          sha = "c2",
          message = "this is the first feature's implementation"
        ),
        Commit(
          sha = "c3",
          message = "this is a commit that is not matched"
        ),
        Commit(
          sha = "c4",
          message = "this is the second feature's implementation"
        ),
        Commit(
          sha = "c5",
          message = "this is an in-progress feature"
        )
      )
    )

    "works" {
      createPlan(config, log) shouldBe Plan(
        actions = listOf(
          Action(command = Command.TAG, sha = "c1", refname = "feature1-start"),
          Action(command = Command.TAG, sha = "c2", refname = "feature1-finish"),
          Action(command = Command.TAG, sha = "c2", refname = "feature2-start"),
          Action(command = Command.TAG, sha = "c4", refname = "feature2-finish")
        )
      )
    }
  }
}
