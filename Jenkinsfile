Boolean mockCmdb = true
List supportGroups = [
    'RT-SRE',
    'RT-IA',
]
List supportPermissions = [
    'hudson.model.Item.Build',
    'hudson.model.Item.Cancel',
    'hudson.model.Item.Read',
    'com.cloudbees.plugins.credentials.CredentialsProvider.Create',
    'com.cloudbees.plugins.credentials.CredentialsProvider.Update',
]
List lbus = []
List lbuPermissions = [
    'hudson.model.Item.Read',
    'hudson.model.Item.Build',
    'hudson.model.Item.Cancel',
]
List lbuResults = []

pipeline {
    agent any

    options {
        skipDefaultCheckout()
        timestamps()
    }

    stages {
        stage('Setup') {
            steps {
                checkout scm
                script {
                    if (mockCmdb) {
                        def cmdbLbusFile = readJSON file: 'cmdb_mock/lbus.json'
                        lbuResults = cmdbLbusFile.results
                        echo lbuResults.toString()
                    } else {
                        String url = 'https://cmdb.pru.intranet.asia/rest/lbus/'
                        while (url != null) {
                            def response = httpRequest url: url, quiet: true
                            def content = readJSON text: response.content

                            lbuResults += content.results
                            url = content.next
                        }
                    }

                    lbuResults.each { lbu ->
                        echo "lbu: ${lbu.ad_code}"
                        lbus.add([
                            name: lbu.ad_code,
                            displayName: lbu.ad_code.toUpperCase(),
                            groups: ['Carl (b27d3455-19c0-40e4-b25d-8e207e81cf84)']
                        ])
                    }
                }
            }
        }
        stage('Seed') {
            steps {
                checkout scm
                jobDsl(
                    targets: ['seed.groovy'].join('\n'),
                    failOnMissingPlugin: true,
                    failOnSeedCollision: true,
                    removedConfigFilesAction: 'DELETE',
                    removedJobAction: 'DELETE',
                    removedViewAction: 'DELETE',
                    lookupStrategy: 'JENKINS_ROOT',
                    sandbox: false,
                    additionalParameters: [
                        lbus: lbus,
                        lbuPermissions: lbuPermissions,
                        supportGroups: supportGroups,
                        supportPermissions: supportPermissions,
                    ]
                )
            }
        }
    }
}
