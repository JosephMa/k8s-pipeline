node {
     def artiServer
     def rtMaven
     def buildInfo
     def tagName
     def sshServer
     def workspace = pwd()
     def gitlab_url = "http://172.17.0.5:80"
     properties([
           gitLabConnection(gitlab_url),
           pipelineTriggers([
                 [
                       $class               : 'GenericTrigger',
                       triggerOnPush        : true,
                       triggerOnMergeRequest: true,
                 ]
           ]),
           disableConcurrentBuilds(),
           overrideIndexTriggers(false)
     ])

     stage('Prepare') {
         echo env.BRANCH_NAME
         // Variables initilization
         artiServer = Artifactory.server('jfrog-artifactory')
         rtMaven = Artifactory.newMavenBuild()
         buildInfo = Artifactory.newBuildInfo()
         // Build Env
         buildInfo.env.capture = true
         rtMaven.deployer releaseRepo:'automation-mvn-solution-local', snapshotRepo:'automation-mvn-sol-snapshot-local', server: artiServer
         rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: artiServer
         rtMaven.tool = "mvn"
         // Build SSH Server
         withCredentials([usernamePassword(credentialsId: 'host-ssh', passwordVariable: 'sshPassword', usernameVariable: 'sshUser')]) {
            sshServer = getSSHServer(sshUser,sshPassword)
            echo workspace
            //sshCommand remote: sshServer, command: "cd "+workspace
         }

         // Remove resources created previous time
         try {
            sshCommand remote: sshServer, command: "docker rm cloud-app"
            sshCommand remote: sshServer, command: "docker rmi joseph/cloud-app"
            sshCommand remote: sshServer, command: "kubectl -s --namespace=devops delete deploy --all"
            sshCommand remote: sshServer, command: "kubectl -s --namespace=devops delete svc --all"
            sshCommand remote: sshServer, command: "kubectl -s --devops delete configmap --al"
            sh 'sleep 5'
         } catch(Exception e) {
            println('remove resources in kubernetes failed, please check the log.')
         }
         echo "stage 00"
     }
     stage('Checkout Source') {
         echo "stage 01"
         //git url: 'https://github.com/JosephMa/k8s-pipeline.git', branch: 'master'
         git url: gitlab_url+'/root/k8s-pipeline.git', branch: 'master'
     }
     stage('Build Maven') {
         echo "stage 02"
         // Maven build
         // rtMaven.run pom: 'pom.xml', goals: 'clean test install', buildInfo: buildInfo
         try {
             withMaven(maven: 'maven3.6.3') {
                sh "mvn -B clean package -DskipTests"
                sh "pwd"
             }
         }
         catch (err) {
             throw err
         }
         echo "build complete!"
     }
     stage('Checkout Docker') {
        echo "stage 03"
     }
     stage('Build Image') {
        echo "stage 04"
        // Docker tag and upload to snapshot repository
        //def artDocker = Artifactory.docker server: artiServer
        //artDocker.push(tagName, 'docker-stage', buildInfo)
        //artiServer.publishBuildInfo buildInfo
        //buildInfo = artDocker.push tagName, 'docker-stage'

        def server_url = "127.0.0.1:8081"
        def repo = "docker-stage"
        tagName = "joseph/cloud-app:" + env.BUILD_NUMBER
        def tagImage = "${server_url}" + "/" + "${repo}"+ "/" + "${tagName}"
        echo tagImage
        docker.build(tagName)
        sleep 5

        withCredentials([usernamePassword(credentialsId: 'docker-register', passwordVariable: 'dockerPassword', usernameVariable: 'dockerUser')]) {
            sh "docker login -u ${dockerUser} -p ${dockerPassword} 127.0.0.1:8081"
            sh "docker tag ${tagName} ${tagImage}"
            sh "docker push ${tagImage}"
            sh "docker rmi ${tagImage}"
        }
     }
     stage('Build and Deploy') {
        echo "stage 05"
     }
     stage('Testing App') {
        echo "stage 06"
        // Smoke test
        sh "docker run --name cloud-app -d -p 8181:8080 ${tagName}"
        sleep 3
        sshCommand remote: sshServer, command: "curl http://127.0.0.1:8181"
        sh "docker stop cloud-app"
        sh "docker rm cloud-app"
        //sh "docker rmi ${tagName}"
     }
     stage('Promotions') {
        echo "stage 07"
        sh 'sed -i "s/{tag}/${BUILD_ID}/g" docker-promote.json'
        sh 'curl  -X POST -H \"Content-Type: application/json\"  http://172.27.244.233:8082/artifactory/docker-stage/v2/promote -d @docker-promote.json -u ops01:AP6BUJfR9Yz2wiZBUwJtWZoTrTt'
     }
     stage('Distribute') {
     	echo "stage 08"
     	//sh 'curl -O -u ops01:AP6BUJfR9Yz2wiZBUwJtWZoTrTt -X GET http://localhost:8082/artifactory/kube-config/1.0/app.cfg'
        //sh 'kubectl -s kube-master:8080 --namespace=devops create configmap app-config --from-literal=$(cat app.cfg)'
        sshCommand remote: sshServer, command: "pwd && cd "+workspace + " && pwd"
        sshCommand remote: sshServer, command: "kubectl --namespace=devops create configmap app-config --from-file=./app.cfg"
     }
     stage('Deployment') {
     	echo "stage 09"
     	sh 'echo $(pwd)'
        sh 'sed -i "s/{tag}/${BUILD_ID}/g" kube-app.json'
        sh 'sleep 10'
        //sh 'kubectl -s kube-master:8080 create -f kube-svc.json'
        //sh 'kubectl -s kube-master:8080 create -f kube-app.json'
        sshCommand remote: sshServer, command: "kubectl create -f kube-svc.json"
        sshCommand remote: sshServer, command: "kubectl create -f kube-app.json"
        sh 'for i in {1..120}; do echo "waiting for app starting,$[180-$i] second left..."; sleep 1; done;'
        sh 'echo deploy finished successfully.'
     }
     stage('Notice') {
     	echo "stage 10"
     	echo 'please visit http://localhost:8181 to verify the result.'
     }
}

def getSSHServer(sshUser,sshPassword){
    def remote = [:]
    remote.name = 'host-172.27.244.233'
    remote.host = '172.27.244.233'
    remote.user = sshUser
    remote.port = 2222
    remote.password = sshPassword
    remote.allowAnyHosts = true
    return remote
}
