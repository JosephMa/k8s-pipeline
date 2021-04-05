node {
     def artiServer
     def rtMaven
     def buildInfo
     def tagName
     //def remote = [:]
     //remote.name = 'host'
     //remote.host = '172.20.50.10'
     //remote.user = 'root'
     //remote.port = 22
     //remote.password = ''
     //remote.allowAnyHosts = true

     //def mvnHome = tool 'maven3.6.3'
     //env.PATH = "${mvnHome}/bin:${env.PATH}"

     stage('Prepare') {
         // Variables initilization
         artiServer = Artifactory.server('jfrog-artifactory')
         rtMaven = Artifactory.newMavenBuild()
         buildInfo = Artifactory.newBuildInfo()
         // Build Env
         buildInfo.env.capture = true
         rtMaven.deployer releaseRepo:'automation-mvn-solution-local', snapshotRepo:'automation-mvn-sol-snapshot-local', server: artiServer
         rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: artiServer
         rtMaven.tool = "mvn"

         echo "stage 00"
     }
     stage('Checkout Source') {
         echo "stage 01"
         git url: 'https://github.com/JosephMa/k8s-pipeline.git', branch: 'master'
         //sshScript remote: remote, script: "git url: 'https://github.com/JosephMa/k8s-pipeline.git', branch: 'master'"
         //sshScript remote: remote, script: "pwd"
     }
     stage('Build Maven') {
         echo "stage 02"
         // Maven build
         // rtMaven.run pom: 'pom.xml', goals: 'clean test install', buildInfo: buildInfo
         withMaven(maven: 'maven3.6.3') {
            //sh "mvn clean install -Dmaven.test.skip=true"
            def exitValue = sh(script: "nohup mvn clean install -Dmaven.test.skip=true > /dev/null 2>&1 &", returnStatus: true)
            echo "return exitValue :${exitValue}"
            if(exitValue != 0)
            {
               error("Failure")
            }
         }
         sh "ls -l target"
         echo "build complete!"
     }
     stage('Checkout Docker') {
        echo "stage 03"
     }
     stage('Build Image') {
        echo "stage 04"
        // Docker tag and upload to snapshot repository
        tagName = 'joseph/cloud-app:' + env.BUILD_NUMBER
        docker.build(tagName)
        def artDocker= Artifactory.docker('ops02', 'AP51rcczx4RvqFz3Uc5jnH7bLFH')
        //def artDocker = Artifactory.docker server: artiServer
        artDocker.push(tagName, 'docker-stage', buildInfo)
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
            sh 'curl "http://127.0.0.1:8181"'
        }
     }
     stage('Promotions') {
        echo "stage 07"
        sh 'sed -i "s/{tag}/${BUILD_ID}/g" docker-promote.json'
        sh 'curl  -X POST -H \"Content-Type: application/json\"  http://localhost:8082/artifactory/docker-stage/v2/promote -d @docker-promote.json -u ops02:AP51rcczx4RvqFz3Uc5jnH7bLFH'
     }
     stage('Distribute') {
     	echo "stage 08"
     	sh 'curl -O -u ops02:AP51rcczx4RvqFz3Uc5jnH7bLFH -X GET http://localhost:8082/artifactory/kube-config/1.0/app.cfg'
        sh 'kubectl -s kube-master:8080 --namespace=devops create configmap app-config --from-literal=$(cat app.cfg)'
     }
     stage('Deployment') {
     	echo "stage 09"
     	sh 'echo $(pwd)'
        sh 'sed -i "s/{tag}/${BUILD_ID}/g" kube-app.json'
        sh 'sleep 10'
        sh 'kubectl -s kube-master:8080 create -f kube-svc.json'
        sh 'kubectl -s kube-master:8080 create -f kube-app.json'
        sh 'for i in {1..120}; do echo "waiting for app starting,$[180-$i] second left..."; sleep 1; done;'
        sh 'echo deploy finished successfully.'
     }
     stage('Notice') {
     	echo "stage 10"
     	echo 'please visit http://localhost:8181 to verify the result.'
     }
}
