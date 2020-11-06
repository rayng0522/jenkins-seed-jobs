jobs.each { job ->
    UUID uuid = UUID.randomUUID()
    String lbu        = job.ad_code
    String appRef     = job.code
    String gitRepo    = job.repo
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
            // customise the branch project factory
            it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory") << {
                // pipeline jobs will have their script path set to `pipelines/customPipeline.groovy`
                scriptPath("Jenkinsfile")
            }
        }
    }
}
