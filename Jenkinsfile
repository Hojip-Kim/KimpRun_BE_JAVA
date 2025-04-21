pipeline {
  agent any
  stages {
    stage('Checkout & Test') {
      steps {
        checkout scm
        sh './gradlew test'
      }
    }
  }
}