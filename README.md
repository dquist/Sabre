# CivFactions #

Welcome to the CivFactions development team. My goal here is to create a more enjoyable, reliable and focused civilization minecraft server. Civcraft is a great concept and has been very successful, but it is plagued by poor leadership and lack of innovation. I aim to take and improve upon the ideas that made civcraft so popular while fixing the many mistakes that it made.

### Confidential ###

By adding you to this repo, it means that I trust you. Please do not violate this trust. All the information here is considered private and should not be shared outside this group. For the time being this is a closed-source project, although it may be open-sourced post launch.

### Schedule ###

I'm hoping for launching a month-long open beta server on 1/1/2016 with a full release a month or two after. The open beta will allow us to iron out any major bugs and balancing issues.

## Getting Set Up ##

The project can be logically divided into two distinct parts, the server and the Sabre plugin.

### Server ###

The server repo contains everything required to run the actual minecraft server minus things like world data and log files. It has all the binaries and configuration files so that everyone on the team can set up their own development server and know they are running the same configuration as everyone else.

### Sabre ###

Sabre is the main plugin for the server and replaces the following civcraft plugins:

* Citadel
* PrisonPearl
* FactoryMod
* RealisticBiomes
* JukeAlert
* Bastion
* Humbug

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

* Jira is used to track issues and bugs
* Each project has two main branches: master and develop
* All work should be done in feature branches off of develop
* When a feature branch is ready to be merged, create a pull-request and I will merge it
* Develop will be merged into master on each major release
* For more reference, visit http://nvie.com/posts/a-successful-git-branching-model/

\- Gordon