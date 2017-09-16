package test.acceptance.grh

fun jarCmd(arguments: String): String {
  val userDir = System.getProperty("user.dir")
  // TODO: This doesn't respect DEV_VERSION env var, but that should probably go away anyway
  // See https://www.pivotaltracker.com/story/show/151146169
  return "java -jar $userDir/build/libs/git-revisionist-historian-dev.jar $arguments"
}
