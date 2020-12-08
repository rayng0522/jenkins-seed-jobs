library identifier: 'jenkins-shared-libraries@tags/0.0.4', retriever: modernSCM([
    $class: 'GitSCMSource',
    remote: 'https://github.com/rayng0522/jenkins-shared-libraries.git',
    credentialsId: '309ff56c-7993-4ed0-8003-dbdf15fb3f01'
])


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
                    serverUrl(bitbucketServerUrl)
                    credentialsId(bitbucketGitCredential)
                    repoOwner(bitbucketRepoOwner)
                    repository(blueprintRepoName)
                    traits {
                        'com.cloudbees.jenkins.plugins.bitbucket.BranchDiscoveryTrait'{
                            strategyId(1)
                        }
                        'com.cloudbees.jenkins.plugins.bitbucket.ForkPullRequestDiscoveryTrait'{
                            strategyId(1)
                        }
                        'com.cloudbees.jenkins.plugins.bitbucket.OriginPullRequestDiscoveryTrait'{
                            strategyId(1)
                        }
                    }
                }
            }

            it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.extended.RemoteJenkinsFileWorkflowBranchProjectFactory") << {
                remoteJenkinsFile("Jenkinsfile")
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
        email_notification("SUCCESSFUL", ["ntwairay@gmail.com"])
    }
}
