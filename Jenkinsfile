#!groovy
@Library('github.com/cloudogu/ces-build-lib@b273cea')
import com.cloudogu.ces.cesbuildlib.*

properties([
  // Keep only the most recent builds in order to preserve space
  buildDiscarder(logRotator(numToKeepStr: '20')),
  // Don't run concurrent builds for a branch, because they use the same workspace directory
  disableConcurrentBuilds()
])

node {

  catchError {

    def mvnHome = tool 'M3'
    def javaHome = tool 'JDK8'

    Maven mvn = new MavenLocal(this, mvnHome, javaHome)
    Git git = new Git(this)


    stage('Checkout') {
      checkout scm
      git.clean('')
    }

    initMaven(mvn)

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
      def sonarQube = new SonarQube(this, 'sonarcloud.io')
      sonarQube.updateAnalysisResultOfPullRequestsToGitHub('sonarqube-gh')
      sonarQube.isUsingBranchPlugin = true

      sonarQube.analyzeWith(mvn)

      if (!sonarQube.waitForQualityGateWebhookToBeCalled()) {
        currentBuild.result ='UNSTABLE'
      }
    }

    stage('Deploy') {
      if (preconditionsForDeploymentFulfilled()) {

        mvn.setDeploymentRepository('ossrh', 'https://oss.sonatype.org/', 'de.triology-mavenCentral-acccessToken')

        mvn.setSignatureCredentials('de.triology-mavenCentral-secretKey-asc-file',
                                    'de.triology-mavenCentral-secretKey-Passphrase')

        mvn.deployToNexusRepositoryWithStaging()
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

boolean preconditionsForDeploymentFulfilled() {
  if (isBuildSuccessful() &&
      !isPullRequest() &&
      shouldBranchBeDeployed()) {
    return true
  } else {
    echo "Skipping deployment because of branch or build result: currentResult=${currentBuild.currentResult}, " +
      "result=${currentBuild.result}, branch=${env.BRANCH_NAME}."
    return false
  }
}

private boolean shouldBranchBeDeployed() {
  return env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop'
}

private boolean isBuildSuccessful() {
  currentBuild.currentResult == 'SUCCESS' &&
    // Build result == SUCCESS seems not to set be during pipeline execution.
    (currentBuild.result == null || currentBuild.result == 'SUCCESS')
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
