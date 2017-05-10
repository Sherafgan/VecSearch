# VecSearch: Getting  Started
### How to run VecSearch demo?

To run VecSearch server and access it through browser (localhost:8080) we need to:

 - have [Git](https://git-scm.com/) 2.7 or later version installed.
 - have [Maven](https://maven.apache.org/) 3 or later version installed.
 - [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 8 or later version installed.
 - have PostgreSQL 9.4 or later version installed, with database called ```postgres``` for the ```postgres``` user with ```postgres``` password. This is required to have SQL table with detailed (url, timestamps, etc) information about the synthetic dataset of videos.
 - have vector similarity DBMS called 'Vectorization' (later explained how to install).
 - have VecSearch sources.

Now that the requirements above are met:
1. Create a simple directory where all of the VecSearch project files will be placed. Here and further on, we show how to do this in Unix systems providing bash commands.
```sh
$ mkdir VecSearchFiles
```
2. Place in the created directory the sources of the VecSearch, which most likely is the directory named 'VecSearch'.
```sh
$ mv -r PATH/TO/VecSearch/ /PATH/TO/VecSearchFiles/
```
(So far we have directory VecSearchFiles with VecSearch directory in it)
3. Go to 'VecSearchFiles' directory
```sh
$ cd VecSearchFiles
```
4. Run the sql dump to fill the local PostgreSQL database called ```postgres``` with data.
```sh
$ sudo -u postgres psql postgres < /VecSearch/data/videos_dump.sql
```
5. Now we install the 'Vectorization' DBMS locally.
```sh
$ git clone https://github.com/Sherafgan/vectorization-1.git
$ cd vectorization-1
$ mvn clean package
$ mvn install
$ cd ..//
```
6. We then run the installed 'Vectorization' database's server in the ```VecSearch``` directory to have all the DB's files indexed there. And we have ```Vectorization``` database's logs in ```VectorizationDBMS.log``` file in ```VecSearch``` directory, so we could see the status of the DB server and the indexing.
```sh
$ cd VecSearch
$ nohup java -jar ..//vectorization-1/similarity-database-node/target/similarity-database-node-0.0.5-SNAPSHOT.jar > VectorizationDBMS.log 2>&1 &
```
7. To index the data to 'Vectorization' database, first we change the ```pom.xml``` file placed in ```VecSearch``` directory by setting the main class from ```Main``` to ```indexing.Indexer``` probably in the ```27```th line of the ```pom.xml```.
```sh
$ nano pom.xml
```
After changing, click ```Ctrl+X```, enter ```Y``` and press ```Enter```.
8. Now we build the VecSearch project to have run indexing.
```sh
$ mvn clean compile assembly:single
$ java -jar target/VecSearch-0.7-SNAPSHOT-jar-with-dependencies.jar
```
9. After indexing, now we can the demo.
-First, we in ```pom.xml``` file change main class from ```indexing.Indexex``` back to ```Main``` (similar steps as in ```7```th step).
-Then, again
```sh
$ mvn clean compile assembly:single
$ nohup java -jar target/VecSearch-0.7-SNAPSHOT-jar-with-dependencies.jar > server.log 2>&1 &
```
After a couple of minutes (required to load Word2Vec models, etc.) the VecSearch server should be running and accessible through browser with address ```localhost:8080```.
(The logs of the VecSearch server is available in ```server.log```)