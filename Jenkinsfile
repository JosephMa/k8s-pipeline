pipeline {
  agent any
  stages {
    stage('clean env') {
      parallel {
        stage('clean env') {
          steps {
            echo 'test'
            sh 'nohup echo "this is test" &'
          }
        }
        stage('clean env1') {
          steps {
            sh 'nohup echo "this is clean env1" &'
          }
        }
      }
    }
    stage('switch') {
      input {
        message 'Should we continue?'
        id 'Yes, we should.'
        submitter 'alice,bob'
        parameters {
          string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
        }
      }
      steps {
        echo "Hello, ${PERSON}, nice to meet you."
      }
    }
    stage('build code') {
      steps {
        sh '''echo "build code start" sleep 10 echo "build code finish"'''
      }
    }
    stage('stg deployment') {
      parallel {
        stage('deployment') {
          steps {
            timeout(time: 50, activity: true) {
              sh '''echo "start deploy" sleep 60 echo "deploy success"'''
            }

          }
        }
        stage('stg deployment1') {
          steps {
            sh '''echo "error deployment" exit 1 echo "error deployment exit"'''
          }
        }
      }
    }
    stage('prd deployment') {
      steps {
        sh '''echo "start prd deployment " sleep 10 echo "prd deployment success"'''
      }
    }
  }
}
