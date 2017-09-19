package test.acceptance.grh.cli

import io.kotlintest.matchers.include
import io.kotlintest.matchers.should
import io.kotlintest.specs.StringSpec
import test.acceptance.grh.jarCmd

// NOTE: Requires a push-able clone of test repo to exist - ssh key is in ci/credentials.yml
// NOTE: This does NOT run on CI, see ci/tasks/cli-processor-acceptance-test.yml
//       for issues.  It should instead be converted to just create a local/remote
//       pair of repos on the fly, to avoid all the permissions issues.
//       See https://www.pivotaltracker.com/story/show/151149103
class CliProcessorAcceptanceTest : StringSpec() {
  init {
    "works" {
      val userDir = System.getProperty("user.dir")
      val cmd = jarCmd(
          "--processor cli"
      )
      val output = com.thewoolleyweb.grh.processhelper.runInDir(
        cmd,
        "$userDir/../git-revisionist-historian-test-repo"
      )

      val outputString = output.joinToString("\n")

      outputString should include("$ git tag --force feature3-finish ba05591c3e0595f005ae77e6b0ac0722b1588c90")
      outputString should include("$ git tag --force feature3-start fa4f5d259b90fca8798fa31080cf6918f07353b5")
      outputString should include("$ git tag --force feature2-finish fa4f5d259b90fca8798fa31080cf6918f07353b5")
      outputString should include("$ git tag --force feature2-start e09016485ec4b2ec9b36d36210b2c09df0f8bef1")
      outputString should include("$ git tag --force feature1-finish e09016485ec4b2ec9b36d36210b2c09df0f8bef1")
      outputString should include("$ git tag --force feature1-start 891efbddf4bcbb5db2369b502771c11569ff0881")
      outputString should include("$ git push --force --tags")
    }
  }
}
