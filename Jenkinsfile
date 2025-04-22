pipeline {
  agent any

  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Test')     { steps { sh './gradlew clean test' } }
  }

  post {
    success {
      githubNotify credentialsId: 'github-app',
                   context:       'ci/jenkins',
                   status:        'SUCCESS',
                   description:   "Build #${BUILD_NUMBER} passed",
                   targetUrl:     BUILD_URL
    }
    failure {
      githubNotify credentialsId: 'github-app',
                   context:       'ci/jenkins',
                   status:        'FAILURE',
                   description:   "Build #${BUILD_NUMBER} failed",
                   targetUrl:     BUILD_URL
    }
  }
}
