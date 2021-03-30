pipeline {
    agent {
        docker {
            image 'python:3.5.2'
            args '-v $HOME/tools/docker'
        }
    }
    stages {
        stage('Checkout Maven') {
            steps {
                sh 'python --version'
                echo "stage 01"
            }
        }
        stage('Build Maven') {
            steps{
                echo "stage 02"
            }
        }
        stage('Checkout Docker') {
            steps{
                echo "stage 03"
            }
        }
        stage('Build Image') {
            steps{
                echo "stage 04"
            }
        }
        stage('Build and Deploy') {
            steps{
                echo "stage 05"
            }
        }
       stage('Testing App') {
            steps{
                echo "stage 06"
            }
       }
       stage('Promotions') {
            steps{
                echo "stage 07"
            }
       }
        stage('Distribute') {
            steps{
                echo "stage 08"
            }
        }
       stage('Deployment') {
            steps{
                echo "stage 09"
            }
       }
       stage('Notice') {
            steps{
                echo "stage 10"
            }
       }
    }
}
