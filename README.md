# OpenShift Next Demo
***

## Purpose

The purpose of this is to demonstrate the introduction of an OpenShift containerized implementation into an existing legacy infrastructure.   We will illustrate how to continue running your current legacy applications as they are while adding on to them and migrating portions over to a new containerized OpenShift environment.

In order to achieve this, we will first install Wildfly/JBoss and deploy a simple web application packaged as a war.  This application has an HTML/JavaScript front end calling restful Java WebServices to display various 'hello world' messages.

![Legacy Application](./plusonelegacy/src/main/webapp/images/graph1.png  "Legacy Application")

Once the legacy application is up and running, we will introduce new services implemented as Java services and Bonjour services.  These new services are taken from the OpenShift MSA Demo <https://github.com/redhat-helloworld-msa> and will be deployed in an OpenShift. Once deployed, these services will be integrated in to the front end of the existing legacy application as shown below.

![Hybrid Application](./plusonelegacy/src/main/webapp/images/graph2.png  "Hybrid Application")

Now that we have the two technologies up and running side by side, we can start to migrate existing legacy portions of the application over to OpenShift.  In this case, we can continue to run the old legacy services in parallel until all consumers have migrated over to the new OpenShift service and then decommission it.

![Hybrid Application](./plusonelegacy/src/main/webapp/images/graph3.png  "Hybrid Application")

## Prerequisites
- **Linux**
	- This demo was created and tested using Fedora 24 and RHEL 7.3.
- **Java Runtime**
	- version 1.8
- **Docker**
	- version 10 <https://docs.docker.com/engine/installation/>
	- add ```INSECURE_REGISTRY='--insecure-registry 172.30.0.0/16'``` to /etc/sysconfig/docker and restart Docker

- **Ansible**
	- version 2.3
	*At time of writing (2016/11/28), some Linux distro's had version 2.2 in their repositories and in order to get 2.3 you have to run from source* <http://docs.ansible.com/ansible/intro_installation.html#running-from-source>.
	- The host running ansible must be able to remotely connect to both the server running a standalone 'legacy' web application in wildfly and the server that will be running OpenShift.  Please read the following for more information <http://docs.ansible.com/ansible/intro_getting_started.html#id5>
- **Git**
	- version 2.7

## Download the Demo
Clone the github repository

	$ git clone https://github.com/rduncan506/plusone.git

## Configure Demo
To install both the legacy and OpenShift portions of the demo, some information is required.  First edit the hosts files and set the IP addresses for the hosts to install the two applications.

```bassh
$ vi <plusoneROOT>/ansible/plusonedemo/hosts
```

> [legacy-servers]  
  ***192.168.223.43***
>
  [ocp-servers]  
> ***192.168.223.43***

Now modify the following to match your environment

```bassh
$ vi <plusoneROOT>/ansible/plusonedemo/group_vars/user-vars.yaml
```

>  \# A temporary directory for any required temporary downloads
>  \# Must be a directory with read/write permissions  
>  tmp_dir: ***/tmp***
>
>  \# The location of the cloned PlusOne github repository
>  \# Must be a directory with read/write permissions  
>  plusone_home: ***/home/user/development/plusone***
>
>  \# The location to install wildfly
>  \# Must be a directory with read/write permissions  
>  wildfly_home: ***/opt/testwildfly***
>  
>  \# Automatically install the legacy application  
>  install_legacy_application: ***true***
>
>  \# The location to install the OpenShift Client tools (oc)
>  \# Must be a directory with read/write permission  
>  oc_install: ***/opt/testopenshift***
>  
>  \# Automatically install the MSA services  
>  install_msa_application: ***true***

## Option 1 - From Binaries
To make it easier and remove dependencies on build tools, all source has been compiled/packaged and has been downloaded when cloning the github repository above.

### Step 1 - Run Ansible to Install the Legacy Application
In order to make the install quick and easy, the following Ansible Playbook can be executed to get the legacy application up and running.

The Playbook will perform the following

1. Check if Wildfly exists at the location given above, if so, step 2 is skipped.
2. Download and install Wildfly (<http://download.jboss.org/wildfly/10.0.0.Final/wildfly-10.0.0.Final.zip>)
3. Deploy the precompiled war file downloaded as part of the git clone above (<plusoneROOT>/plusonelegacy/bin/plusone-legacy-application.war)

```bassh
	$ cd <plusoneROOT>/ansible/plusonedemo
	$ ansible-playbook -i hosts legacy.yml
```

Once the script has completed the Legacy Application will be accessible via <http://legacy-host:8080/plusone-legacy-application/index-legacy.html>

Also, you may access the Wildfly admin console via <http://legacy-host:9990> and follow the instructions there to add users to enable the console.

### Step 2 - Run Ansible to Install OpenShift Services
In this demo, we will be using ```oc cluster up``` <https://github.com/openshift/origin/blob/master/docs/cluster_up_down.md#overview>.

**Please read <https://github.com/openshift/origin/blob/master/docs/cluster_up_down.md#linux> and ensure all steps prior to the ```oc cluster up``` command have been performed**

The Playbook will perform the following tasks

1. Check if the ```oc``` exists in the previously configured location, if so, step to is skipped
2. Download and install the ```oc``` command
3. Download and start the OpenShift docker image
4. Deploy 3 pre-built MSA services from <plusoneROOT>/plusonemsa/* using the Dockerfiles and binaries contained within
5. Expose the services so they are accessible

```bassh
	$ cd <plusoneROOT>/ansible/plusonedemo
	$ ansible-playbook -i hosts openshift.yml
```

While the script is running, the OpenShift console will be accessible once the ```oc cluster up``` command has completed.  It can be accessed at ***http://< ocp-server>:8443/console*** with user: developer and password: developer.  As the ansible script progresses, a helloworld-msa project will appear.

## Option 2 - From Source
###Additional Prerequisites
In addition to the main prerequisites above, the following are required to compile and deploy the source in the following sections.

- **Maven**
	- version >= 3.3
- **NPM**
	- version >= 2.15

### Step 1 - Build and Deploy Legacy Application

With Wildfly or JBoss running, execute the following

```bassh
$ cd <plusoneROOT>/plusonelegacy
$ mvn clean install wildfly:deploy
```

### Step 2 - Download the Source for the MSA services

```bassh
oc login <ocp-server>:8443 -u developer -p developer
```
[Build and Deploy Hola Service](https://cdn.rawgit.com/redhat-helloworld-msa/helloworld-msa/master/readme.html#_deploy_hola_jax_rs_wildfly_swarm_microservice)

```bassh
$ cd <MSAWorkingDirectory>
$ git clone https://github.com/redhat-helloworld-msa/hola
$ cd hola/
$ oc new-build --binary --name=hola -l app=hola
$ mvn package; oc start-build hola --from-dir=. --follow
$ oc new-app hola -l app=hola,hystrix.enabled=true
$ oc expose service hola
```

[Build and Deploy Ola ](https://cdn.rawgit.com/redhat-helloworld-msa/helloworld-msa/master/readme.html#_deploy_ola_spring_boot_microservice)

```bassh
$ cd <MSAWorkingDirectory>
$ git clone https://github.com/redhat-helloworld-msa/ola
$ cd ola/
$ oc new-build --binary --name=ola -l app=ola
$ mvn package; oc start-build ola --from-dir=. --follow
$ oc new-app ola -l app=ola,hystrix.enabled=true
$ oc expose service ola
```

[Build and Deploy Bonjour](https://cdn.rawgit.com/redhat-helloworld-msa/helloworld-msa/master/readme.html#_deploy_bonjour_nodejs_microservice)

```bassh
$ cd <MSAWorkingDirectory>
$ git clone https://github.com/redhat-helloworld-msa/bonjour
$ cd bonjour/
$ oc new-build --binary --name=bonjour -l app=bonjour
$ npm install; oc start-build bonjour --from-dir=. --follow
$ oc new-app bonjour -l app=bonjour
$ oc expose service bonjour
```
## Running through the Demo

<http://localhost:8080/plusone-legacy-application/services.json>

```bassh
$ vi <wildfly_home>/wildfly-10.0.0.Final/welcome-content/services.json
```

>{  
  "hello-service": {  
    "url": "http://localhost:8080/plusone-legacy-application/rest/hello"  
  },  
  "hola-move-service": {  
    "url": "http://hola-helloworld-msa.192.168.223.43.xip.io/api/hola"  
  },  
  "hola-legacy-service": {  
    "url": "http://localhost:8080/plusone-legacy-application/rest/hola"  
  },  
  "bonjour-service": {  
    "url": "http://bonjour-helloworld-msa.192.168.223.43.xip.io/api/bonjour"  
  },  
  "ola-service": {  
    "url": "http://ola-helloworld-msa.192.168.223.43.xip.io/api/ola"  
  }  
>}


### Legacy Application

<http://localhost:8080/plusone-legacy-application/index-legacy.html>

### Hybrid Application

<http://localhost:8080/plusone-legacy-application/index-hybrid.html>

### Migrating

<http://localhost:8080/plusone-legacy-application/index-move.html>

## Troubleshooting


## Notes
	$ kill -9 `fuser -f nohup.out`
	$ docker stop origin `docker ps -q`
