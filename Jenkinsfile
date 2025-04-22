pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Test') {
      steps {
        sh './gradlew clean test'
      }
    }
  }

  // github로 상태 전송 (커밋 메시지 형태)
  post {
    success {
      githubNotify context: 'ci/jenkins',
                   status:  'SUCCESS',
                   description: "Build #${env.BUILD_NUMBER} passed",
                   targetUrl:   "${env.BUILD_URL}"
    }
    failure {
      githubNotify context: 'ci/jenkins',
                   status:  'FAILURE',
                   description: "Build #${env.BUILD_NUMBER} failed",
                   targetUrl:   "${env.BUILD_URL}"
    }
  }
}
