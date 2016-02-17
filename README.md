JSONLD-Java Clerezza Integration module
=======================================

[![Build Status](https://travis-ci.org/jsonld-java/jsonld-java-clerezza.svg?branch=master)](https://travis-ci.org/jsonld-java/jsonld-java-clerezza) 
[![Coverage Status](https://coveralls.io/repos/jsonld-java/jsonld-java-clerezza/badge.svg?branch=master)](https://coveralls.io/r/jsonld-java/jsonld-java-clerezza?branch=master)

This module provide a `ParsingProvider`and `SerializingProvider` for Apache Clerezza. Those Providers plug into the Clerezza `Parser` and `Serializer` service infrastructure. Meaning that adding this bundle will allow Clerezza to parse and serialize JSON-LD.

USAGE
=====

From Maven
----------

    <dependency>
        <groupId>com.github.jsonld-java</groupId>
        <artifactId>jsonld-java-clerezza</artifactId>
        <version>0.8.1</version>
    </dependency>

(Adjust for most recent <version>, as found in ``pom.xml``).

ClerezzaTripleCallback
------------------

The ClerezzaTripleCallback returns an instance of `org.apache.clerezza.rdf.core.MGraph`

See [ClerezzaTripleCallbackTest.java](./src/test/java/com/github/jsonldjava/clerezza/ClerezzaTripleCallbackTest.java) for example Usage.


From OSGI
---------

Assuming the above Bundle is active in the OSGI Environment one can simple inject the `Serializer` and/or `Parser` service.

    @Reference
    private Serializer serializer;

    @Reference
    private Parser parser;


Normal Java
-----------

Both the `Parser` and `Serializer` also support `java.util.ServiceLoader`. So when running outside an OSGI environment one can use the `getInstance()` to obtain an instance.

    Serializer serializer = Serializer.getInstance();

    Parser parser = Parser.getInstance();

Supported Formats
-----------------

The JSON-LD parser implementation supports `application/ld+json`. The serializer supports both `application/ld+json` and `application/json`.

The rational behind this is that the parser can not parse any JSON however the Serializer does generate valid JSON.
