#!groovy
@Library('github.com/cloudogu/ces-build-lib@e59ce46')
import com.cloudogu.ces.cesbuildlib.*

node {

  properties([
    // Keep only the most recent builds in order to preserve space
    buildDiscarder(logRotator(numToKeepStr: '20')),
    // Don't run concurrent builds for a branch, because they use the same workspace directory
    disableConcurrentBuilds()
  ])

  catchError {

    def mvnHome = tool 'M3'
    def javaHome = tool 'JDK8'

    Maven mvn = new MavenLocal(this, mvnHome, javaHome)
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

      // Pull Requests are analyzed locally, so no calling of the QGate webhook
      if (!isPullRequest()) {
        timeout(time: 1, unit: 'HOURS') {
          // This will only work if a webhook to <JenkinsInstance>/sonarqube-webhook/ is set up in SQ project
          // See https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
          def qgate = waitForQualityGate()
          if (qgate.status != 'OK') {
            echo "Quality Gate failure: ${qgate.status} --> Build UNSTABLE"
            currentBuild.result = 'UNSTABLE'
          }
        }
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
