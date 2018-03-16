folder('deployments');

[
  [
    name:       'httpd',
    repository: 'https://github.com/Artiax/helm.git',
    branch:     'master'
  ]
].each { Map deployment ->
  pipelineJob("deployments/${deployment.name}") {
    parameters {
      stringParam('RELEASE_NAME','','Release name for helm deployment. Also used as a prefix to all kubernetes components.')
      stringParam('TAG','latest','Docker image build tag to use for this deployment.')
    }
    definition {
      cpsScm {
        scm {
          git {
            branch("${deployment.branch}")
            remote {
              url("${deployment.repository}")
            }
          }
        }
        scriptPath("Jenkinsfile")
      }
    }
    configure {
      it / definition / lightweight(true)
    }
  }
}
