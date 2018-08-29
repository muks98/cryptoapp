pipeline {
  agent any
  stages {
    stage('tag image') {
      steps {
        openshiftImageStream(name: 'caps-template-service', tag: 'latest', namespace: 'caps-template-service-dev', apiURL: 'https://console.appcanvas.net:8443/console/project/caps-template-service-dev/browse/images', authToken: '123', verbose: 'e')
      }
    }
    stage('') {
      steps {
        openshiftVerifyDeployment(depCfg: 'caps-template-service', apiURL: 'https://url1.com', authToken: '29499', namespace: 'caps-template-service-dev', replicaCount: '1')
      }
    }
  }
}