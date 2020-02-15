String label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label,
            cloud: "openshift",
            containers: [containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat')]) {
    try {
        node(label) {
            timeout(time: 30, unit: 'MINUTES') {
                stage("environment info") {
                    sh 'printenv'
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