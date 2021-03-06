library identifier: 'jenkins-shared-libraries@tags/0.0.4', retriever: modernSCM([
    $class: 'GitSCMSource',
    remote: 'https://github.com/rayng0522/jenkins-shared-libraries.git',
    credentialsId: '309ff56c-7993-4ed0-8003-dbdf15fb3f01'
])


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
List jobs = []
List lbuPermissions = [
    'hudson.model.Item.Read',
    'hudson.model.Item.Build',
    'hudson.model.Item.Cancel',
]
List lbuResults = []
List jobResults = []
Map lbuGroups = [
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
    thlife: ['GTHLife-JenkinsUsers (6c32432c-6075-482a-8441-026315fb827d)'],
    vnlife: ['GVNLife-JenkinsUsers (1c25a673-ae6e-463d-ba60-8dffef92a8bd)'],
]

def fileRequest(filename){
  def file = readJSON file: filename
  List results = file.results
  return results
}

def dataRequest(url){
    List results
    while (url != 'null') {
      echo "Requesting from ${url}"
      def response = httpRequest url: url, quiet: true, ignoreSslErrors: true
      def content = readJSON text: response.content

      results += content.results
      url = content.next
    }
    return results
}

pipeline {
    // options {
    //     skipDefaultCheckout()
    //     timestamps()
    // }
    agent any
    environment {
        HTTP_PROXY  = 'http://10.163.39.77:8080'
        HTTPS_PROXY = 'http://10.163.39.77:8080'
        NO_PROXY    = 'intranet.asia,pru.intranet.asia'
    }

    stages {
        stage('Setup') {
            steps {
                checkout scm
                script {
                    if (mockCmdb) {
                        lbuResults = fileRequest('cmdb_mock/lbus.json')
                        echo lbuResults.toString()
                        jobResults = fileRequest('cmdb_mock/jobs.json')
                        echo jobResults.toString()
                    } else {
                        lbuResults = dataRequsest('1',results)
                    }

                    lbuResults.each { lbu ->
                        lbus.add([
                            name: lbu.ad_code,
                            displayName: lbu.ad_code.toUpperCase(),
                            groups: lbuGroups.get(lbu.ad_code)
                        ])
                    }

                    jobResults.each { job ->
                        def folderName  = ['RT-SRE/blueprints', job.subscription.tenant.lbu.ad_code, job.code].join('/')
                        jobs.add([
                            adCode: job.subscription.tenant.lbu.ad_code,
                            appRef: job.code,
                            blueprintGitRepoUrl: job.repo,
                            folderName: folderName,
                            appOwner: job.owner,
                            existingJob: jenkins.model.Jenkins.instance.getItemByFullName(folderName) != null ? true : false
                        ])
                    }
                }
            }
        }
        stage('Seed') {
            steps {
                checkout scm
                echo "Seeding: ${lbus}"
                jobDsl(
                    targets: ['seed.groovy'].join('\n'),
                    failOnMissingPlugin: true,
                    failOnSeedCollision: true,
                    removedConfigFilesAction: 'IGNORE',
                    removedJobAction: 'IGNORE',
                    removedViewAction: 'IGNORE',
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
        stage('Multibranch project') {
            steps {
                checkout scm
                script {
                    echo "Creating multibranch project jobs"
                    def result = jobDsl(
                        targets: ['multibranch.groovy'].join('\n'),
                        additionalParameters: [
                            jobs: jobs,
                            failOnMissingPlugin: true,
                            failOnSeedCollision: true,
                            removedConfigFilesAction: 'IGNORE',
                            removedJobAction: 'IGNORE',
                            removedViewAction: 'IGNORE',
                            blueprintsFolder: 'RT-SRE/blueprints',
                            remoteJenkinsfileGitRepoUrl: "https://github.com/rayng0522/jenkins-seed-jobs.git",
                            gitCredential: 'ntwairay'
                        ]
                    )
                }
            }
        }
    }
    post {
        success {
            script {
                def newJobs = jobs.findAll { it.existingJob == false }
                newJobs.each { job ->
                    def customBody = "New seed job ${job.folderName} has been created"
                    email_notification("SUCCESSFUL", [job.appOwner], customBody)
                }
            }
        }
    }
}
