## OpenShift Next Deployment

- Requires Ansible 2.3 or newer
- Expects CentOS/RHEL 7.x hosts

There are 2 separate and independent playbooks, legacy.yml and openshift.yml.

#### legacy.yml
- installs/starts a very basic implementation of WildFly Application Server
version 10
- can deploy a 'legacy' monolithic application to the Wildfly instance

#### openshift.yml
- installs/starts OpenShift within docker using the 'oc cluster up' command
- can create/build/deploy 3 microservices to be used within the 'legacy' application mentioned above

To use them, first edit the "hosts" inventory file to contain the
hostnames of the machines on which you want JBoss and OpenShift deployed. If running against remote hosts, remote user will have to be set in both playbooks.

Secondly, edit the group_vars/user-vars.yaml file to set environment parameters.

Then run the playbooks, like this:

	ansible-playbook -i hosts legacy.yml
	ansible-playbook -i hosts openshift.yml
