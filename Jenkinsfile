node {
     def mvnHome
     def artiServer
     def rtMaven
     def buildInfo
     def tagName
     def remote = [:]
     remote.name = 'host'
     remote.host = '172.20.61.160'
     remote.user = 'root'
     remote.port = 22
     remote.password = '123456'
     remote.allowAnyHosts = true

     stage('Prepare') {
         // Variables initilization
         artiServer = Artifactory.server('jfrog-artifactory')
         rtMaven = Artifactory.newMavenBuild()
         buildInfo = Artifactory.newBuildInfo()
         // Build Env
         buildInfo.env.capture = true
         rtMaven.deployer releaseRepo:'automation-mvn-solution-local', snapshotRepo:'automation-mvn-sol-snapshot-local', server: artiServer
         rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: artiServer
         rtMaven.tool = "maven"

         echo "stage 00"
     }
     stage('Checkout Source') {
         echo "stage 01"
         //git(url: 'https://github.com/JosephMa/k8s-pipeline.git', credentialsId: 'github_token', branch: "master")
         git url: 'https://github.com/JosephMa/k8s-pipeline.git', branch: 'master'
         //sshScript remote: remote, script: "git url: 'https://github.com/JosephMa/k8s-pipeline.git', branch: 'master'"
         //sshScript remote: remote, script: "pwd"
     }
     stage('Build Maven') {
         echo "stage 02"
         // Maven build

         rtMaven.run pom: 'pom.xml', goals: 'clean test install', buildInfo: buildInfo
     }
     stage('Checkout Docker') {
        echo "stage 03"
     }
     stage('Build Image') {
        echo "stage 04"
        // Docker tag and upload to snapshot repository
        tagName = 'joseph/openapi-demo:' + env.BUILD_NUMBER
        docker.build(tagName)
        //def artDocker= Artifactory.docker('admin', 'ACWmSmLjLc5VKVYuSeumtarKV7TfboRAEwC1tqKAUvbniFJqp7xLfCyvJ7GxWuJZ')
        def artDocker = Artifactory.docker server: artiServer
        artDocker.push(tagName, 'release-local-docker', buildInfo)
        artiServer.publishBuildInfo buildInfo
     }
     stage('Build and Deploy') {
        echo "stage 05"
     }
     stage('Testing App') {
        echo "stage 06"
        // Smoke test
        docker.image(tagName).withRun('-p 8181:8080') {
            sleep 5
            // NOTE: According to business logic
            sh 'curl "http://127.0.0.1:8181"'
        }
     }
     stage('Promotions') {
        echo "stage 07"
     }
     stage('Distribute') {
     	echo "stage 08"
     }
     stage('Deployment') {
     	echo "stage 09"
     }
     stage('Notice') {
     	echo "stage 10"
     	echo 'please visit http://localhost:8181 to verify the result.'
     }
}
