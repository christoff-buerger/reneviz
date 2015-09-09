 
The archive cornell-vivo.zip contains the Cornell VIVO data as an Apache Jena TSV database.


To load the data use the Apache Jena Fuseki Server available at

  http://jena.apache.org/

In order to supply Reneviz with a data source, start fuseki with the following command

  ./fuseki-server --loc [location of database] --port 3131 /cornell
  
Once the server is running a web client is available at

  http://localhost:3131
