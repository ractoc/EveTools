pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh 'mvn clean verify'
            }
        }
        stage('deploy') {
            steps {
                sh 'unzip -d deployment/target/deploy deployment-*.zip'
                sh 'deployment/target/deploy/installAll.sh'
            }
        }
    }
  post {
    success {
      emailext (
          attachLog: true,
          compressLog: true,
          subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: """<p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
            <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
          recipientProviders: [developers()]
        )
    }

    failure {
      emailext (
          attachLog: true,
          compressLog: true,
          subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
          body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
            <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
          recipientProviders: [developers()]
        )
    }
  }
}
