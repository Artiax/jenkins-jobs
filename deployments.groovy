folder('deployments');

[


  [ name: 'httpd', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'master' ],


].each { Map deployment ->
  pipelineJob("deployments/${deployment.name}") {
      parameters {
          stringParam('RELEASE_NAME','','Release name for helm deployment. Also used as a prefix to all kubernetes components.')
          stringParam('TAG','')
      }
      definition {
          cps {
              script(readFileFromWorkspace('templates/deployment.groovy'))
              sandbox(true)
          }
      }
  }
}
