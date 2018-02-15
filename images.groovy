folder('images');

[


  [name: 'jenkins', repository: 'https://github.com/Artiax/jenkins.git', branch: 'master'],
  [name: 'jenkins-slave', repository: 'https://github.com/Artiax/jenkins-slave.git', branch: 'master'],
  [name: 'httpd', repository: 'https://github.com/Artiax/httpd.git', branch: 'master'],


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
              scriptPath("Jenkinsfile")
          }
      }
      configure {
         it / definition / lightweight(true)
      }
  }
}
