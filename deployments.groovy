folder('deployments');

[


  [ name: 'jenkins', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'development' ],
  [ name: 'httpd', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'development' ],


].each { Map deployment ->
  pipelineJob("deployments/${deployment.name}") {
      parameters {
          stringParam('TAG','default')
      }
      definition {
          cps {
              script($/
                  echo 'Spawning a slave for this job...'

                  node('docker') {
                      stage('Clone') {
                          git branch: '${deployment.branch}', url: '${deployment.repository}', changelog: false, poll: false
                      }

                      stage('Deploy') {
                          dir('deployments') {
                              sh 'sed -ri "s/(image:.*${deployment.name}:)[a-z0-9\\.]*/\\1$$TAG/" ${deployment.name}.yaml'
                              sh 'kubectl apply --record -f ${deployment.name}.yaml'
                          }
                      }
                  }
              /$.stripIndent())
              sandbox(true)
          }
      }
  }
}
