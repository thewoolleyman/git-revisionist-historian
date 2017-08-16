package test.grh.config

import com.thewoolleyweb.grh.config.GrhConfig
import com.thewoolleyweb.grh.config.IncrementCommit
import com.thewoolleyweb.grh.git.*
import com.thewoolleyweb.grh.plan.Plan
import com.thewoolleyweb.grh.plan.createPlan
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class createPlanTest : StringSpec() {
  init {
    val config = GrhConfig(
      remote = "origin",
      branchToRevise = "solution",
      incrementCommits = listOf(
        IncrementCommit(
          message = "Initial commit",
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
        steps = listOf(
          Invocation(command = Command.TAG, sha = "c1", tag = "feature1-start"),
          Invocation(command = Command.TAG, sha = "c2", tag = "feature1-finish"),
          Invocation(command = Command.TAG, sha = "c2", tag = "feature2-start"),
          Invocation(command = Command.TAG, sha = "c4", tag = "feature2-finish"),
          Invocation(command = Command.PUSH_TAGS)
        )
      )
    }
  }
}
