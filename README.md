# Sabre Plugin (CivFactions) #

Repository for the Sabre plugin which powers the CivFactions server.

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

    - civfactions
      - eclipse
        - Sabre  <-- Clone civfactions/sabre here
        - CivLobby
      - factions   <-- Clone civfactions/factions here

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
