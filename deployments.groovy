folder('deployments');

[


  [ name: 'httpd', repository: 'https://github.com/Artiax/helm.git', branch: 'master' ],


].each { Map deployment ->
  pipelineJob("deployments/${deployment.name}") {
      parameters {
          stringParam('RELEASE_NAME','','Release name for helm deployment. Also used as a prefix to all kubernetes components.')
          stringParam('TAG','')
      }
      definition {
          cps {
              script("""
                node('docker') {
                    stage('Clone') {
                        git branch: '${deployment.branch}', url: '${deployment.repository}', changelog: false, poll: false
                    }

                    stage('Deploy') {
                        dir('charts') {
                            sh '''
                                TILLER_PORT=\$(kubectl get svc -n kube-system tiller -o jsonpath='{.spec.ports[].port}')
                                helm upgrade \${RELEASE_NAME} ./${deployment.name} --install --set deployment.imageTag=\${TAG} --host tiller.kube-system.svc.cluster.local:\${TILLER_PORT}
                            '''
                        }
                    }
                }
              """.stripIndent())
              sandbox(true)
          }
      }
  }
}
