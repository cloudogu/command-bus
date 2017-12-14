#!groovy
@Library('github.com/cloudogu/ces-build-lib@888733b')
import com.cloudogu.ces.cesbuildlib.*

properties([
  // Keep only the most recent builds in order to preserve space
  buildDiscarder(logRotator(numToKeepStr: '20')),
  // Don't run concurrent builds for a branch, because they use the same workspace directory
  disableConcurrentBuilds()
])

node {

  catchError {

    Maven mvn = new MavenInDocker(this, "3.5.0-jdk-8")
    Git git = new Git(this)


    stage('Checkout') {
      checkout scm
      /* Don't remove folders starting in "." like
     * .m2 (maven), .npm, .cache, .local (bower)
     */
      git.clean('".*/"')
    }

    initMaven(mvn)
    withCredentials([usernamePassword(credentialsId: 'de.triology-mavenCentral-acccessToken',
      passwordVariable: 'password', usernameVariable: 'username')]) {
      echo "username=$username"
    }
  }

  // Archive Unit and integration test results, if any
  junit allowEmptyResults: true,
    testResults: '**/target/surefire-reports/TEST-*.xml, **/target/failsafe-reports/*.xml'

  // Find maven warnings and visualize in job
  warnings consoleParsers: [[parserName: 'Maven']]

  mailIfStatusChanged(getCommitAuthorOrDefaultEmailRecipients(env.EMAIL_RECIPIENTS_COMMAND_BUS))
}

boolean preconditionsForDeploymentFullfilled() {
  if (currentBuild.currentResult == 'SUCCESS' && currentBuild.result == 'SUCCESS' && env.BRANCH_NAME == 'master') {
    return true
  } else {
    echo "Skipping deployment because of branch or build result: currentResult=${currentBuild.currentResult}, " +
      "result=${currentBuild.result}, branch=${env.BRANCH_NAME}."
    return false
  }
}

void initMaven(Maven mvn) {

  if ("master".equals(env.BRANCH_NAME)) {

    echo "Building master branch"
    mvn.additionalArgs = "-DperformRelease"
    currentBuild.description = mvn.getVersion()
  }
}

String getCommitAuthorOrDefaultEmailRecipients(String defaultRecipients) {
  def isStableBranch = env.BRANCH_NAME in ['master', 'develop']
  String commitAuthorEmail = new Git(this).commitAuthorEmail

  if (commitAuthorEmail == null && commitAuthorEmail.isEmpty())
    return defaultRecipients
  if (isStableBranch) {
    if (!defaultRecipients.contains(commitAuthorEmail)) {
      defaultRecipients += ",$commitAuthorEmail"
    }
    return defaultRecipients
  } else {
    return commitAuthorEmail
  }
}

void writeSettingsXmlWithServer(def serverId, def serverUsername, def serverPassword) {
  script.writeFile file: "${env.HOME}/.m2/settings.xml", text: """
<settings>
    <servers>
        <server>
          <id>$serverId</id>
          <username>$serverUsername</username>
          <password>$serverPassword</password>
        </server>
    </servers>
</settings>"""
}

void deployToMavenCentral(Maven mvn) {
  withCredentials([file(credentialsId: 'de.triology-mavenCentral-publicKeyring-file', variable: 'pubring'),
                   file(credentialsId: 'de.triology-mavenCentral-secretKeyring-file', variable: 'secring'),
                   string(credentialsId: 'de.triology-mavenCentral-secretKey-Passphrase', variable: 'passphrase'),
                   usernamePassword(credentialsId: 'de.triology-mavenCentral-acccessToken',
                     passwordVariable: 'password', usernameVariable: 'username')]) {

    // The deploy plugin does not provide an option of passing server credentials via command line
    // So, create settings.xml that contains custom properties that are can be set via command line (property
    // interpolation) - https://stackoverflow.com/a/28074776/1845976
    writeSettingsXmlWithServer('ossrh', '$ossrh.username', '$ossrh.password')

    // TODO Is settings.xml picked up or do we need to use -s?
    mvn "deploy -P release " +
      // gpg params for signing jar
      "-Dgpg.publicKeyring=$publicKeyring -Dgpg.secretKeyring=$privateKeyring -Dgpg.passphrase=$passphrase " +
      // credentials for deploying to sonatype
      "-Dossrh.username=$username -Dossrh.password=$password "
  }
}
