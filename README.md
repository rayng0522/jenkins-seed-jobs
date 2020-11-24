# blueprint-seed-job

This repository contains the seed job and associated code to create the blueprint deployer jobs for each LBU within Jenkins.

## Folders

The seed job creates folders for each LBU under a blueprints folder (`/RT-SRE/blueprints`) which is also created.

Each LBU is discovered by querying the CMDB for the LBUs list (`/rest/lbus/`), and iterating over that list; pagenation of the CMDB is supported.

Each folder is assigned permissions, with each the AD Groups being referenced by a static Map (`lbuGroups`) in the Jenkinsfile. If an LBU does not have an entry in the `lbuGroups` map, additional groups are not added.

## Jobs

The seed job creates multibranch project for each appRef under the LBU folder of the appRef inside the blueprints folder (`/RT-SRE/blueprints`).

Each multibranch project will have the remote Jenkinsfile plugin enable by default

Each mulitbranch project inherits the permission from the parent folder

The basic details of the appRef saves in the field display name and description
