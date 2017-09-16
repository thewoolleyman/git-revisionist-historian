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

      outputString should include("$ git tag --force feature1-start 040c17a02a49aeb38509b09863d1b4b25c11fc46")
      outputString should include("$ git tag --force feature1-finish 5b2dde544098740fa18833ac0dca0e4276932b55")
      outputString should include("$ git tag --force feature2-start 5b2dde544098740fa18833ac0dca0e4276932b55")
      outputString should include("$ git tag --force feature2-finish b49a8d6c545950e4e1a302693cc1fa5a13c636c3")
      outputString should include("$ git tag --force feature3-start b49a8d6c545950e4e1a302693cc1fa5a13c636c3")
      outputString should include("$ git tag --force feature3-finish 2f1517e51473aee3be2d2281e79b3d121700dcea")
      outputString should include("$ git push --force --tags")
    }
  }
}
