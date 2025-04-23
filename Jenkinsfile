pipeline {
  agent any

  environment {
    REG      = "hojipkim/kimprun-back"

    SHA      = sh(script: "git rev-parse --short=7 HEAD",
                  returnStdout: true).trim()
    TAG      = "${BRANCH_NAME}-${SHA}-${BUILD_NUMBER}"
    IMAGE    = "${REG}:${TAG}"

    SSH_ID   = BRANCH_NAME == 'main' ? 'ssh-prod' : 'ssh-dev'
    SSH_PT   = BRANCH_NAME == 'main' ? '2223'     : '2222'
    RUN_PT   = BRANCH_NAME == 'main' ? '8080'     : '8081'
  }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Test')     { steps { sh './gradlew clean test' } }

    stage('Build & Push') {
      steps {
        // docker-registry 크레덴셜로 로그인한 후 build -> push 진행
        docker.withRegistry('https://index.docker.io/v1/', 'docker-registry') {
          def img = docker.build("${IMAGE}")
          img.push()

          // main브랜치면 latest 태그 한번더 push
          script {
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
            ssh -p ${SSH_PT} -o StrictHostKeyChecking=no jenkins@127.0.0.1 \
              '/home/jenkins/deploy.sh ${IMAGE} ${RUN_PT}'
          """
        }
      }
    }
  }

  post {
    success {
      githubNotify credentialsId: 'github-app',
                   context: 'ci/jenkins',
                   status:  'SUCCESS',
                   description: "Build #${BUILD_NUMBER} passed",
                   targetUrl: BUILD_URL
    }
    failure {
      githubNotify credentialsId: 'github-app',
                   context: 'ci/jenkins',
                   status:  'FAILURE',
                   description: "Build #${BUILD_NUMBER} failed",
                   targetUrl: BUILD_URL
    }
  }
}
