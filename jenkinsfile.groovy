String label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label,
            cloud: "openshift",
            containers: [
                    containerTemplate(
                            name: 'jnlp',
                            image: 'jenkins/jnlp-slave:3.35-5-alpine'),
                    containerTemplate(
                            name: 'maven',
                            image: 'registry.redhat.io/openshift3/jenkins-agent-maven-35-rhel7',
                            ttyEnabled: true,
                            command: 'cat')
            ]
) {
    try {
        node(label) {
            timeout(time: 10, unit: 'MINUTES') {
                container('maven') {

                    stage("environment info") {
                        sh 'printenv'
                    }

                    stage('checkout') {
                        checkout scm
                    }

                    stage('build and test') {
                        try {
                            sh 'source /usr/local/bin/scl_enable && mvn -s maven.settings.xml clean package'
                        } finally {
                            junit '**/target/surefire-reports/*.xml'
                        }
                    }

                    stage('stash artifacts') {
                        stash name: "api", includes: "**/target/*.jar"
                        stash name: "osobj", includes: "openshift/**/*"
                    }
                }
            }
        }

        node {
            timeout(time: 10, unit: 'MINUTES') {

                stage("environment info") {
                    sh 'printenv'
                }

                stage("build docker") {
                    unstash "api"
                    unstash "osobj"
                    sh "oc process -f openshift/hello-world-docker-build.yml | oc apply --force -f -"
                    // target/hello-world-1.0-SNAPSHOT.jar
                    sh 'oc help'
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