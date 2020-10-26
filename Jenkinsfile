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
List lbuGroups = [
    afprho: ['GAFRHO-JenkinsUsers (43d97a45-b778-4478-877c-699a75618810)'],
    hklife: ['GHKLife-JenkinsUsers (17428b7d-2483-4092-afa6-32e538cdf6a9)'],
    hkprho: ['GHKRHO-JenkinsUsers (8e89731f-0ca5-4968-aa11-e576a7525856)'],
    idlife: ['GIDLife-JenkinsUsers (995ad555-cbb6-47d1-a612-413f9a15f27a)'],
    khlife: ['GKHLife-JenkinsUsers (6d3620cf-36f7-412e-9951-f0bb7a139559)'],
    lalife: ['GLALife-JenkinsUsers (973bde84-75c1-4c42-ba3c-8304abf76dec)'],
    mmlife: ['GMMLIFE-JenkinsUsers (8ac3fdec-67b8-4713-a15d-a824862dae02)'],
    mylife: ['GMYLife-JenkinsUsers (49a32dea-6e65-4d47-8c08-9e6fcd42ecfb)'],
    phlife: ['GPHLife-JenkinsUsers (0a9f8d98-002a-4236-9f90-217c89041be9)'],
    sgesiv: ['GSGEIS-JenkinsUsers (01ea966c-cd73-41c5-9b92-fffd692feb15)'],
    sglife: ['GSGLife-JenkinsUsers (568a0ac7-dd7a-4a75-a87b-7f9b7576eb90)'],
    sgprho: ['GSGRHO-JenkinsUsers (7c38d674-8615-4d62-ae8b-dc507a2df53e)'],
    sgrtss: ['GSGRITS-JenkinsUsers (6b66fb47-ff0b-4434-8cbe-1440f3afaab9)'],
    tflife: ['GTHLife-JenkinsUsers (6c32432c-6075-482a-8441-026315fb827d)'],
    vnlife: ['GVNLife-JenkinsUsers (1c25a673-ae6e-463d-ba60-8dffef92a8bd)'],
]

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
                            groups: lbuGroups.get(lbu.ad_code)
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
