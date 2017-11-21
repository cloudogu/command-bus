#!groovy
@Library('github.com/cloudogu/ces-build-lib@b695209')
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

    initMaven(mvn, 'sonarqube-gh', git.gitHubRepositoryName)

    stage('Build') {
      mvn 'clean install -DskipTests'
      archive '**/target/*.jar'
    }

    stage('Unit Test') {
      mvn "test"
    }

    stage('Integration Test') {
      mvn "verify -DskipUnitTests"
    }

    stage('Statical Code Analysis') {
      withSonarQubeEnv('sonarcloud.io') {
        mvn "$SONAR_MAVEN_GOAL -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_AUTH_TOKEN $SONAR_EXTRA_PROPS " +
          //exclude generated code in target folder
          "-Dsonar.exclusions=target/**"
      }

      waitForQualityGateAndSetBuildResult('UNSTABLE')
    }

    stage('Deploy') {
      if ((preconditionsForDeploymentFullfilled())) {
        deployToMavenCentral(mvn)
      }
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

void initMaven(Maven mvn, String sonarGitHubCredentials, String gitHubrepoName) {

  if ("master".equals(env.BRANCH_NAME)) {

    echo "Building master branch"
    mvn.additionalArgs = "-DperformRelease"
    currentBuild.description = mvn.getVersion()

  } else {

    if (isPullRequest()) {

      echo "Building PR ${env.CHANGE_ID} at branch ${env.BRANCH_NAME}"

      // See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
      mvn.additionalArgs = "-Dsonar.analysis.mode=preview "
      mvn.additionalArgs += "-Dsonar.github.pullRequest=${env.CHANGE_ID} "
      mvn.additionalArgs += "-Dsonar.github.repository=$gitHubrepoName "
      withCredentials([string(credentialsId: sonarGitHubCredentials, variable: 'PASSWORD')]) {
        mvn.additionalArgs += "-Dsonar.github.oauth=${env.PASSWORD} "
      }

    } else {

      echo "Building branch ${env.BRANCH_NAME}"

      // Run SQ analysis in specific project for feature, hotfix, etc.
      // See https://docs.sonarqube.org/display/PLUG/Branch+Plugin
      // Note that -Dsonar.branch is deprecated from SQ 6.6: https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
      mvn.additionalArgs = "-Dsonar.branch.name=$env.BRANCH_NAME -Dsonar.branch.target=master"
    }
  }
}

boolean isPullRequest() {
  // CHANGE_ID == pull request id
  // http://stackoverflow.com/questions/41695530/how-to-get-pull-request-id-from-jenkins-pipeline
  env.CHANGE_ID != null && env.CHANGE_ID.length() > 0
}

/**
 * Blocks until a webhook is called on Jenkins that signalizes finished SonarQube QualityGate evaluation.
 * If the Quality Gate fails the build status is set to {@code buildResultOnQualityGateFailure}.
 *
 * If there is no webhook or SonarQube does not respond within 2 minutes, the build fails.
 * So make sure to set up a webhook in SonarQube global administration or per project to
 * {@code <JenkinsInstance>/sonarqube-webhook/}.
 * See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
 *
 * If this build is a Pull Request, this does not wait, because usually PRs are analyzed locally.
 * See https://docs.sonarqube.org/display/PLUG/GitHub+Plugin
 */
void waitForQualityGateAndSetBuildResult(String buildResultOnQualityGateFailure) {
  // Pull Requests are analyzed locally, so no calling of the QGate webhook
  if (!isPullRequest()) {
    timeout(time: 2, unit: 'MINUTES') { // Needed when there is no webhook for example
      def qGate = waitForQualityGate()
      if (qGate.status != 'OK') {
        echo "Quality Gate failure: ${qGate.status} --> Build $buildResultOnQualityGateFailure"
        currentBuild.result = buildResultOnQualityGateFailure
      }
    }
  }
}

String getCommitAuthorOrDefaultEmailRecipients(String defaultRecipients) {
  def isStableBranch = env.BRANCH_NAME in ['master', 'develop']
  String commitAuthorEmail = new Git(this).commitAuthorEmail

  if (commitAuthorEmail == null && commitAuthorEmail.isEmpty())
    return defaultRecipients
  if (isStableBranch) {
    if (!defaultRecipients.contains(commitAuthorEmail)) {
      defaultRecipients += ";$commitAuthorEmail"
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
