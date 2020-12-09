jobs.each { job ->
    UUID uuid = UUID.randomUUID()
    String lbu        = job.adCode
    String appRef     = job.appRef
    String folderName = [blueprintsFolder, lbu, appRef].join('/')
    String blueprintGitRepoUrl = job.blueprintGitRepoUrl
    if (jenkins.model.Jenkins.instance.getItemByFullName(folderName) == null) {
        println(folderName)
        emailext (
            subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}'",
            body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
            to: "ntwairay@gmail.com",
            from: "jenkins@example.com"
        )
    }
    def test = multibranchPipelineJob("${folderName}") {
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
    }
}
