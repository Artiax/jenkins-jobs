node('docker') {
    stage('Clone') {
        git branch: '${deployment.branch}', url: '${deployment.repository}', changelog: false, poll: false
    }

    stage('Deploy') {
        dir('charts') {
            sh 'helm upgrade ${RELEASE_NAME} ./${deployment.name} --install --set deployment.imageTag=${TAG} --host tiller.kube-system.svc.cluster.local:44134'
        }
    }
}
