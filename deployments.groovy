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
              script('''#!/bin/bash
                echo &apos;Spawning a slave for this job...&apos;

                node(&apos;docker&apos;) {
                    stage(&apos;Clone&apos;) {
                        git branch: &apos;${deployment.branch}&apos;, url: &apos;${deployment.repository}&apos;, changelog: false, poll: false
                    }

                    stage(&apos;Deploy&apos;) {
                        dir(&apos;charts&apos;) {
                            sh &apos;&apos;&apos;#!/bin/bash
                                TILLER_PORT=$(kubectl get svc -n kube-system tiller -o jsonpath=&apos;{.spec.ports[].port}&apos;)
                                export HELM_HOST=&quot;tiller.kube-system.svc.cluster.local:$TILLER_PORT&quot;
                                helm upgrade ${RELEASE_NAME} ./$(deployment.name} --install --set deployment.imageTag=${TAG}
                            &apos;&apos;&apos;
                        }
                    }
                }
              '''.stripIndent())
              sandbox(true)
          }
      }
  }
}
