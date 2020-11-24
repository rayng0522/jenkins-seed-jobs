jobs.each { job ->
    UUID uuid = UUID.randomUUID()
    String lbu        = job.adCode
    String appRef     = job.appRef
    String folderName = [blueprintsFolder, lbu, appRef].join('/')
    String blueprintGitRepoUrl = job.blueprintGitRepoUrl

    multibranchPipelineJob("${folderName}") {
        displayName "${appRef}"
        description "{'appref': ${appRef}, 'purpose': 'terraform blueprint deployer', 'lbu': ${lbu}  }"
        configure {
            it / sources / 'data' / 'jenkins.branch.BranchSource' << {
                source(class: 'com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource') {
                    id(uuid)
                    serverUrl("https://bitbucket.org")
                    credentialsId("gmail")
                    repoOwner("TinWai")
                    repository("test")
                    traits {
                        'com.cloudbees.jenkins.plugins.bitbucket.BranchDiscoveryTrait'(
                            strategyId(1)
                        )
                    }
                }
            }

            it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.extended.RemoteJenkinsFileWorkflowBranchProjectFactory") << {                    remoteJenkinsFile("Jenkinsfile")
                remoteJenkinsFileSCM(class: 'hudson.plugins.git.GitSCM') {
                     userRemoteConfigs {
                         'hudson.plugins.git.UserRemoteConfig' {
                              url(remoteJenkinsfileGitRepoUrl)
                              credentialsId(gitCredential)
                         }
                     }
                 }
            }
        }
    }
}
