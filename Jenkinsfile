pipeline {
    agent {
        docker {
            image 'python:3.5.2'
            args '-v $HOME/tools/docker'
        }
    }
    stages {
        stage('build') {
            steps {
                sh 'python --version'
            }
        }
        stage('Test') {
            steps {
                sh 'python --version'
            }
        }
    }
}
