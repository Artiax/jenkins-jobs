[


  [name: 'jenkins', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'development'],
  [name: 'jenkins-slave', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'development'],
  [name: 'httpd', repository: 'https://github.com/Artiax/kubernetes.git', branch: 'development'],


].each { Map image ->
  pipelineJob("images/${image.name}") {
      definition {
          cpsScm {
              scm {
                  git {
                      branch("${image.branch}")
                      remote {
                          url("${image.repository}")
                      }
                  }
              }
              scriptPath("images/${image.name}/Jenkinsfile")
          }
      }
      configure {
         it / definition / lightweight(true)
      }
  }
}
