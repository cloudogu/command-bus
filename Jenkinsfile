#!groovy
@Library('github.com/cloudogu/ces-build-lib@c43a1be')
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
      if ((preconditionsForDeploymentFulfilled())) {
        def repo = new Maven.Repository()
        repo.id = 'ces'
        repo.url = 'https://ecosystem.cloudogu.com'
        repo.credentialsIdUsernameAndPassword = 'de.triology-mavenCentral-acccessToken'

        def sigCreds = new Maven.SignatureCredentials()
        sigCreds.publicKeyRingFile = 'de.triology-mavenCentral-publicKeyring-file'
        sigCreds.secretKeyRingFile = 'de.triology-mavenCentral-secretKeyring-file'
        sigCreds.secretKeyPassPhrase = 'de.triology-mavenCentral-secretKey-Passphrase'

        mvn.deployToMavenCentral(sigCreds, repo)
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
  if (currentBuild.currentResult == 'SUCCESS' && currentBuild.result == 'SUCCESS' &&
        env.BRANCH_NAME == 'master' && !isPullRequest()) {
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
