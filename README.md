# CivFactions #

CivFactions Repo.

## Getting Set Up ##

The project can be logically divided into two distinct parts, the server and the Sabre plugin.

### Server ###

The server repo contains everything required to run the actual minecraft server minus things like world data and log files. It has all the binaries and configuration files so that everyone on the team can set up their own development server and know they are running the same configuration as everyone else.

### Sabre ###

Sabre is the main plugin for the server and peforms the functionality of all the following civcraft plugins:

* Citadel
* PrisonPearl
* FactoryMod
* RealisticBiomes
* JukeAlert
* Bastion
* CivChat
* Humbug
* RandomSpawn

Combining the code for all these concepts into a single plugin allows for easier development and ultimately drives a much cleaner and unified gaming experience for the players.

### Developing ###

This repo contains everything required to start developing and debugging the Sabre plugin. 

First, clone both projects to your local workstation with the following directory structure - I recommend using SourceTree because it interfaces nicely with BitBucket. 

    - CivFactions
      - Plugin
        - Sabre  <-- Clone civfactions/sabre here
      - Server   <-- Clone civfactions/server here

The build script is set up to copy the build output directly to the server plugin folder.

Open an eclipse workstation in the CivFactions/Plugin/ directory and import the Sabre project. All the dependencies should already be included as long as you have Java 7 configured.

To build the plugin, open build.ant and click the Run icon. This will build a new jar file in the server plugins directory.

## Workflow ##

* Each project has two main branches: master and develop
* All work should be done in feature branches off of develop
* When a feature branch is ready to be merged, create a pull-request and I will merge it
* Develop will be merged into master on each major release
* Each issue should ideally get its own feature branch.
* For more reference, visit http://nvie.com/posts/a-successful-git-branching-model/

\- Gordon
