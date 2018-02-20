folder('images');

[

  [
    name:       'jenkins',
    repository: 'https://github.com/Artiax/jenkins.git',
    branch:     'master'
  ],
  [
    name:       'jenkins-slave',
    repository: 'https://github.com/Artiax/jenkins-slave.git',
    branch:     'master'
  ],
  [
    name:       'httpd',
    repository: 'https://github.com/Artiax/httpd.git',
    branch:     'master'
  ]

].each { Map image ->
  pipelineJob("images/${image.name}") {
    definition {
      cps {
        sandbox(true)
        script("""
          def image;
          def tag;

          node('docker') {
            stage('Clone') {
              git branch: '${image.branch}', url: '${image.repository}', changelog: false, poll: false
              commit = sh(returnStdout: true, script: "git rev-parse HEAD").trim().take(8)
              timestamp = sh(returnStdout: true, script: "date '+%Y-%m-%d-%H%M'").trim()
              tag = "${timestamp}-${commit}"
            }

            stage('Build') {
              currentBuild.displayName = "${tag}"
              image = docker.build '${image.name}'
            }

            stage('Tag') {

              image.tag "${tag}"
              image.tag "latest"
            }
          }

          timeout(time: 15, unit: 'MINUTES') {
            try {
              input(id: 'deploy', message: 'Deploy this image?')
              build job: 'deployments/${image.name}', parameters: [string(name: 'RELEASE_NAME', value: 'jenkins'),string(name: 'TAG', value: "${tag}")], wait: false
            } catch(err) {
              currentBuild.result = 'SUCCESS'
            }
          }
        """.stripIndent())
      }
    }
  }
}
