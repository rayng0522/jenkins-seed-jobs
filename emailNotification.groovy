#!/usr/bin/env groovy

def call(buildStatus, emailRecipients, customBody="", hasApproval=false) {
    try {
        def icon          = "✅"
        def statusSuccess = true
        def logUrl        = env.RUN_DISPLAY_URL
        def approvalUrl   = env.BUILD_URL + 'input'
        def content       = customBody

        if (content == "") {
            content = "\"${env.JOB_NAME}\" build: ${env.BUILD_NUMBER}\n\nView the log at:\n $logUrl"
        }

        if(buildStatus != "SUCCESSFUL") {
            icon = "❌"
            body = "Job Failed - $content"
            statusSuccess = false
        }
        else {
            if (hasApproval) {
                body = "Job Success - $content\n\nApprove this deployment:\n$approvalUrl"
            }
            else {
                body = "Job Success - $content"
            }
        }

        mail to: emailRecipients.join(","),
            subject: "${icon} [ ${env.JOB_NAME} ] [${env.BUILD_NUMBER}] - ${buildStatus} ",
            body: body

    } catch (e){
        println "ERROR SENDING EMAIL ${e}"
    }
}
