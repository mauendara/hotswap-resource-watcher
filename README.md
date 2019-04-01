hotswap-resource-watcher
==========================
Java process to monitor changes in a directory. Made to be used with hotswap-agent and Wildfly, to sync source and deploy dirs for static resources (currently not working via plugin within hotswap-agent). 

### Hotswap-Agent
Refer to [hotswapagent.org/mydoc_quickstart.html](http://hotswapagent.org/mydoc_quickstart.html) to install DCEVM and hotswap-agent.jar for JDK 8.

### Configuration
Configure source and deploy dirs in `config.properties`.
* original: source directory for which to monitor changes.
* deploy: deploy directory in wildfly in which to copy modified files.

### Wildfly Configuration
Add this options to the start of Wildfly in order to use DCEVM and hotswap-agent:
```
-XXaltjvm=dcevm -javaagent:/path/to/hotswap-agent.jar
```

### Use
To start monitoring changes in a dir start the watcher like this:
```bash
java -jar watcher.jar
```

You can include the directory where `config.properties` as a parameter, in case it isn't in the same directory.
```bash
java -jar watcher.jar /home/user/hotswap-resource-watcher
```
