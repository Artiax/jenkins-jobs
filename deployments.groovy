folder('deployments');

[


  [ name: 'httpd', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'master' ],


].each { Map deployment ->
  pipelineJob("deployments/${deployment.name}") {
      parameters {
          stringParam('TAG','default')
      }
      definition {
          cps {
              script($/
                  node('docker') {
                      stage('Clone') {
                          git branch: '${deployment.branch}', url: '${deployment.repository}', changelog: false, poll: false
                      }

                      stage('Deploy') {
                          dir('charts') {
                              sh '''#!/bin/bash
                                  TILLER_PORT=$(kubectl get svc -n kube-system tiller -o jsonpath='{.spec.ports[].port}')
                                  export HELM_HOST="tiller.kube-system.svc.cluster.local:$TILLER_PORT"
                                  helm upgrade ${RELEASE_NAME} ./${deployment.name} \-\-install \-\-set deployment.imageTag=${TAG}
                              '''
                          }
                      }
                  }
              /$.stripIndent())
              sandbox(true)
          }
      }
  }
}
