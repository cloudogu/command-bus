#!groovy
@Library('github.com/cloudogu/ces-build-lib@1.63.0')
import com.cloudogu.ces.cesbuildlib.*

properties([
  // Keep only the most recent builds in order to preserve space
  buildDiscarder(logRotator(numToKeepStr: '20')),
  // Don't run concurrent builds for a branch, because they use the same workspace directory
  disableConcurrentBuilds()
])

node {
  Maven mvn = new MavenWrapper(this)
  // Sonar stopped support for JRE8 for its client, so for now we run the analysis in a separate container.
  // Once the lib is upgraded to JDK11 this can be removed
  String SonarJreImage = 'adoptopenjdk/openjdk11:jre-11.0.11_9-alpine'
  Git git = new Git(this)

  catchError {

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
      mvn 'test'
    }

    stage('Integration Test') {
      mvn 'verify -DskipUnitTests'
    }

    stage('Static Code Analysis') {
      def sonarQube = new SonarCloud(this, [sonarQubeEnv: 'sonarcloud.io-cloudogu'])

      sonarQube.analyzeWith(new MavenWrapperInDocker(this, SonarJreImage))
      sonarQube.timeoutInMinutes = 5

      if (!sonarQube.waitForQualityGateWebhookToBeCalled()) {
        currentBuild.result ='UNSTABLE'
      }
    }

    stage('Deploy') {
      if (preconditionsForDeploymentFulfilled()) {

        mvn.useDeploymentRepository([id: 'ossrh', url: 'https://oss.sonatype.org/',
                                     credentialsId: 'mavenCentral-acccessToken', type: 'Nexus2'])

        mvn.setSignatureCredentials('mavenCentral-secretKey-asc-file',
                                    'mavenCentral-secretKey-Passphrase')

        mvn.deployToNexusRepositoryWithStaging()
      }
    }
  }

  // Archive Unit and integration test results, if any
  junit allowEmptyResults: true,
    testResults: '**/target/surefire-reports/TEST-*.xml, **/target/failsafe-reports/*.xml'

  mailIfStatusChanged(git.commitAuthorEmail)
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

void initMaven(Maven mvn) {

  if ("master".equals(env.BRANCH_NAME)) {

    echo "Building master branch"
    mvn.additionalArgs = "-DperformRelease"
    currentBuild.description = mvn.getVersion()
  }
}
