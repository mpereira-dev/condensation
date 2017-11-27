# Purpose
I made this project to extract the schema from a MongoDB collection(s).

# Quick Start
* Set your DB properties in application.properties
* Set the collection you wish to condense in the main class Condensation.
* Optionally create a filter query to match on a subset of documents in the collection.
* Run the app and get the output Json schema for your collection.

# Output Format
Currently the app will output a JSON object representing all the keys in the documents in your collection.
Future versions may support an actualy schema format.

# Incongruent Data
Mongo is flexible and it allows you to do things you can't do in a relational database or a strongly typed language like Java.
The app will perform validation on your data and by default will output WARN level messages about incongruencies.
These situations will be marked with MERGE_CONFLICT and are nothing more than placeholders for you to resolve as you see fit.

## Examples of incongruencies: 
* [ "a", 1, {} ] // different types in the same list
* { "a": 1 }, { "a" : {} }// different values for the same key

# Notes
* DBRefs are not yet supported, they throw an exception: 
    * Caused by: org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class com.mongodb.DBRef.