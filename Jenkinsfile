node {
     def mvnHome
     def artiServer
     def rtMaven
     def buildInfo
     def tagName
     stage('Prepare') {
         // Variables initilization
         artiServer = Artifactory.server('jfrog-artifactory')
         rtMaven = Artifactory.newMavenBuild()
         // Build Env
         buildInfo.env.capture = true
         rtMaven.deployer releaseRepo:'automation-mvn-solution-local', snapshotRepo:'automation-mvn-sol-snapshot-local', server: artiServer
         rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: artiServer
         rtMaven.tool = "maven"
         buildInfo = Artifactory.newBuildInfo()
         echo "stage 00"
     }
     stage('Checkout Source') {
         steps {
             //sh 'python --version'
             echo "stage 01"
             git([url: 'git@github.com/JosephMa/k8s-pipeline.git', branch: 'master'])
         }
     }
     stage('Build Maven') {
         steps{
             echo "stage 02"

             // Maven build
             rtMaven.run pom: 'pom.xml', goals: 'clean test install', buildInfo: buildInfo
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
             // Docker tag and upload to snapshot repository
             tagName = 'joseph/openapi-demo:' + env.BUILD_NUMBER
             docker.build(tagName)
             // TODO: Change to artifactory server object
             //def artDocker= Artifactory.docker('admin', 'ACWmSmLjLc5VKVYuSeumtarKV7TfboRAEwC1tqKAUvbniFJqp7xLfCyvJ7GxWuJZ')
             def artDocker = Artifactory.docker server: artiServer
             artDocker.push(tagName, 'release-local-docker', buildInfo)
             artiServer.publishBuildInfo buildInfo
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
              // Smoke test
              docker.image(tagName).withRun('-p 8181:8080') {
                  sleep 5
                  // NOTE: According to business logic
                  sh 'curl "http://127.0.0.1:8181"'
              }
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
              echo 'please visit http://localhost:8181 to verify the result.'
          }
     }
}
