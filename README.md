# blueprint-seed-job

This repository contains the seed job and associated code to create the blueprint deployer jobs for each LBU within Jenkins.

## Folders

The seed job creates folders for each LBU under a blueprints folder (`/RT-SRE/blueprints`) which is also created.

Each LBU is discovered by querying the CMDB for the LBUs list (`/rest/lbus/`), and iterating over that list; pagenation of the CMDB is supported.

Each folder is assigned permissions, with each the AD Groups being referenced by a static Map (`lbuGroups`) in the Jenkinsfile. If an LBU does not have an entry in the `lbuGroups` map, additional groups are not added.

## Jobs

TBD
