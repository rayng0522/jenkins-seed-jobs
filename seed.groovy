// Create the Blueprints folder
String blueprintsFolder = 'RT-SRE/blueprints'
folder(blueprintsFolder) {
    displayName('Blueprints')
    description('Blueprint Pipelines')
}

lbus.each { lbu ->
    // Create the folders
    String folderName = [blueprintsFolder, lbu.name].join('/')
    println ">>> Creating folder ${folderName}"
    folder(folderName) {
        displayName(lbu.displayName)
        description('Blueprint Pipelines for ' + lbu.name)
        configure { folder ->
            folder / 'properties' / 'com.microsoft.jenkins.azuread.AzureAdAuthorizationMatrixFolderProperty' {
                inheritanceStrategy(class: 'org.jenkinsci.plugins.matrixauth.inheritance.InheritParentStrategy')
                // Anonymous
                permission('hudson.model.Item.Discover:anonymous')
                // Support groups
                supportGroups.each { group ->
                    supportPermissions.each { action ->
                        String permissionAction = [action, group].join(':')
                        println "--- ${folderName} Setting permission ${permissionAction}"
                        permission(permissionAction)
                    }
                }
                // LBU groups
                lbu.groups.each { group ->
                    lbuPermissions.each { action ->
                        String permissionAction = [action, group].join(':')
                        println "--- ${folderName} Setting permission ${permissionAction}"
                        permission(permissionAction)
                    }
                }
            }
        }
    }
}
