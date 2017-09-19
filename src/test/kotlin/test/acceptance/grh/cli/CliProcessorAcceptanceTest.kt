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

      outputString should include("$ git tag --force feature1-start 94a5933108e092ce84053435719aaa5e1356bfad")
      outputString should include("$ git tag --force feature1-finish a2eccc4625943b9a523b1894871638276f908209")
      outputString should include("$ git tag --force feature2-start a2eccc4625943b9a523b1894871638276f908209")
      outputString should include("$ git tag --force feature2-finish 49fe2b8bda48d687143ef69ec1d7c85067499dfd")
      outputString should include("$ git tag --force feature3-start 49fe2b8bda48d687143ef69ec1d7c85067499dfd")
      outputString should include("$ git tag --force feature3-finish df6492a6b22271feea618f9c723427b7df0830df")
      outputString should include("$ git push --force --tags")
    }
  }
}
