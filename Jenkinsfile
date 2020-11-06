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

pipeline {
  agent {
    kubernetes {
        label 'pod'
        yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    label: k8-pods
spec:
  containers:
  - name: "git"
    image: "alpine/git"
    command:
    - cat
    tty: true
    alwaysPullImage: true
  - name: "az-cli"
    image: "microsoft/azure-cli"
    command:
    - cat
    tty: true
    alwaysPullImage: true
  - name: "docker-builder"
    image: "docker:19"
    tty: true
    alwaysPullImage: true
    volumeMounts:
      - name: "docker-socket"
        mountPath: "/var/run/docker.sock"
  volumes:
    - name: "docker-socket"
      hostPath:
        path: "/var/run/docker.sock"
"""
    }
  }

    // options {
    //     skipDefaultCheckout()
    //     timestamps()
    // }

    environment {
        HTTP_PROXY  = 'http://10.163.39.77:8080'
        HTTPS_PROXY = 'http://10.163.39.77:8080'
        NO_PROXY    = 'intranet.asia,pru.intranet.asia'
    }

    stages {
        stage('Setup') {
            steps {
                container('jnlp') {
                    checkout scm
                    script {
                        def cmdbLbusFile = readJSON file: 'cmdb_mock/lbus.json'
                        lbuResults = cmdbLbusFile.results
                        echo lbuResults.toString()
                        lbuResults.each { lbu ->
                            lbus.add([
                                name: lbu.ad_code,
                                displayName: lbu.ad_code.toUpperCase(),
                                groups: lbuGroups.get(lbu.ad_code)
                            ])
                        }
                    }
                }
            }
        }
    }
}
