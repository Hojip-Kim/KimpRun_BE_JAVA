pipeline {
  agent any

  environment {
    REG = 'hojipkim/kimprun-back'          // 리터럴만
  }

  stages {
    stage('Init-vars') {
      steps {
        script {
          env.SHA     = sh(script: 'git rev-parse --short=7 HEAD',
                           returnStdout: true).trim()
          env.TAG     = "${env.BRANCH_NAME}-${env.SHA}-${env.BUILD_NUMBER}"
          env.IMAGE   = "${env.REG}:${env.TAG}"
          env.SSH_ID  = env.BRANCH_NAME == 'main' ? 'ssh-prod' : 'ssh-dev'
          env.SSH_PT  = env.BRANCH_NAME == 'main' ? '2223'     : '2222'
          env.RUN_PT  = env.BRANCH_NAME == 'main' ? '8080'     : '8081'
        }
      }
    }

    stage('Checkout') { steps { checkout scm } }
    stage('Test')     { steps { sh './gradlew clean test' } }

    stage('Build & Push') {
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', 'docker-registry') {
            def img = docker.build(env.IMAGE)
            img.push()
            if (env.BRANCH_NAME == 'main') {
              img.push('latest')
            }
          }
        }
      }
    }

    stage('Deploy') {
      when { anyOf { branch 'dev'; branch 'main' } }
      steps {
        sshagent(credentials: [env.SSH_ID]) {
          sh """
            ssh -p ${env.SSH_PT} -o StrictHostKeyChecking=no jenkins@127.0.0.1 \\
              '/home/jenkins/deploy.sh ${env.IMAGE} ${env.RUN_PT}'
          """
        }
      }
    }
  }

  post {
    success {
      githubNotify credentialsId: 'github-app',
                   context: 'ci/jenkins',
                   status: 'SUCCESS',
                   description: "Build #${BUILD_NUMBER} passed",
                   targetUrl: BUILD_URL
    }
    failure {
      githubNotify credentialsId: 'github-app',
                   context: 'ci/jenkins',
                   status: 'FAILURE',
                   description: "Build #${BUILD_NUMBER} failed",
                   targetUrl: BUILD_URL
    }
  }
}
