jobs.each { job ->
    UUID uuid = UUID.randomUUID()
    String lbu        = job.adCode
    String appRef     = job.appRef
    String gitRepo    = job.gitRepo
    String folderName = [blueprintsFolder, lbu, appRef].join('/')
    multibranchPipelineJob("${folderName}") {
        displayName "${appRef}"
        description "{'appref': ${appRef}, 'purpose': 'terraform blueprint deployer', 'lbu': ${lbu}  }"
        configure {
            it / sources / 'data' / 'jenkins.branch.BranchSource' << {
                source(class: 'jenkins.plugins.git.GitSCMSource') {
                    id(uuid)
                    remote(gitRepo)
                    credentialsId(repoCredential)
                    includes('*')
                    excludes('')
                    ignoreOnPushNotifications('false')
                    traits {
                        'jenkins.plugins.git.traits.BranchDiscoveryTrait'()
                    }
                }
            }
            // // customise the branch project factory
            // it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory") << {
            //     // pipeline jobs will have their script path set to `pipelines/customPipeline.groovy`
            //     scriptPath("Jenkinsfile")
            // }

            it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.extended.RemoteJenkinsFileWorkflowBranchProjectFactory") << {
                remoteJenkinsFile("Jenkinsfile2")
                remoteJenkinsFileSCM(class: 'jenkins.plugins.git.GitSCMSource') {
                   configVersion(2)
                    // it / sources / 'userRemoteConfigs' / 'hudson.plugins.git.UserRemoteConfig' << {
                   userRemoteConfigs { // This is the tag of type CONFIGURE
                       'hudson.plugins.git.UserRemoteConfig' (
                              url(gitRepo)
                              credentialsId(repoCredential)
                       )

                   }
                }
            }
        }
    }
}
