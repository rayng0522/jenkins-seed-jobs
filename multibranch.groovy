import javax.mail.*
import javax.mail.internet.*

def sendMail(host, sender, receivers, subject, text) {
    Properties props = System.getProperties()
    props.put("mail.smtp.host", host)
    Session session = Session.getDefaultInstance(props, null)

    MimeMessage message = new MimeMessage(session)
    message.setFrom(new InternetAddress(sender))
    receivers.split(',').each {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(it))
    }
    message.setSubject(subject)
    message.setText(text)

    println 'Sending mail to ' + receivers + '.'
    Transport.send(message)
    println 'Mail sent.'
}

jobs.each { job ->
    UUID uuid = UUID.randomUUID()
    String lbu        = job.adCode
    String appRef     = job.appRef
    String folderName = [blueprintsFolder, lbu, appRef].join('/')
    String blueprintGitRepoUrl = job.blueprintGitRepoUrl
    if (jenkins.model.Jenkins.instance.getItemByFullName(folderName) == null) {
        println(folderName)
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
