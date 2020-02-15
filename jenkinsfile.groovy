String label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label,
            cloud: "openshift",
            containers: [
                    containerTemplate(
                            name: 'jnlp',
                            image: 'jenkins/jnlp-slave:3.35-5-alpine'),
                    containerTemplate(
                            name: 'maven',
                            image: 'maven:3.3.9-jdk-8-alpine',
                            ttyEnabled: true,
                            command: 'cat')
            ],
            volumes: [
                    persistentVolumeClaim(claimName: 'jenkins-maven-cache', mountPath: '/home/jenkins/.m2/repository')
            ]
) {
    try {
        node(label) {
            timeout(time: 30, unit: 'MINUTES') {
                container('maven') {

                    stage("environment info") {
                        sh 'printenv'
                    }

                    stage('checkout') {
                        checkout scm
                    }

                    stage('build') {
                        sh "ls -al"
                        sh 'mvn -s maven.settings.xml clean package -X'
                    }
                }
            }
        }

        currentBuild.result = 'SUCCESS'

    } catch (e) {
        echo "Error occured while executing pipeline: ${e}"
        currentBuild.result = 'FAILURE'
        throw e
    }
}