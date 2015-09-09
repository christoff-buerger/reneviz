RENEVIZ

Prerequisites:

Apache Maven
Apache Jena Fuseki server

recommended: Eclipse IDE for Java EE Developers



Instructions for supplying a rdf data source:

to be able to retrieve data from Reneviz, a data source with a SPARQL interface
has to be present. So far, Reneviz uses the Fuseki server, which is part of the
Apache Jena tools. Fuseki can be downloaded at

  http://jena.apache.org/
  
To start the fuseki server, run

  ./fuseki-server --loc [location of database] --port 3131 /cornell
  
from the fuseki directory. For other data source configurations change

  org.tud.reneviz.data.Constants.getSparqlService()

Instructions for running the project from the command line:

To run the program from the command line, change to the reneviz directory and
run the Maven command
  mvn install

A JBoss server will start up and offer Reneviz under the address

    http://localhost:8080/reneviz/
   
In order to use Reneviz, some initial caching has to be done. This can be
achieved by selecting "create cache" on the initial page. Once the cache is
created, Reneviz can be used via the "RENEVIZ" button. The "SPARQL" button
provides an interface to both the internal jena database (with the clustered
data) and the external database with the full data.




Instructions for running the project in Eclipse EE edition:

To run Reneviz from Eclipse, the source code has to be checked out as a new
project. There are two options to run the project with JBoss:

* Install a JBoss 7.1 server and configure it inside Eclipse. To be able to use
  the JBoss Eclipse server adapters, the JBoss Tools have to be installed.
  See http://www.jboss.org/tools
  
* Run the project with Maven: create a new Run Configuration "Maven Build"
  with the project directory as the working directory and the goal
  "jboss-as:run"



