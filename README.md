JSONLD-Java Clerezza Integration module
=======================================

[![Build Status](https://travis-ci.org/jsonld-java/jsonld-java-clerezza.svg?branch=master)](https://travis-ci.org/jsonld-java/jsonld-java-clerezza) 
[![Coverage Status](https://coveralls.io/repos/jsonld-java/jsonld-java-clerezza/badge.svg?branch=master)](https://coveralls.io/r/jsonld-java/jsonld-java-clerezza?branch=master)

USAGE
=====

From Maven
----------

    <dependency>
        <groupId>com.github.jsonld-java</groupId>
        <artifactId>jsonld-java-clerezza</artifactId>
        <version>0.7.0-SNAPSHOT</version>
    </dependency>

(Adjust for most recent <version>, as found in ``pom.xml``).

ClerezzaTripleCallback
------------------

The ClerezzaTripleCallback returns an instance of `org.apache.clerezza.rdf.core.MGraph`

See [ClerezzaTripleCallbackTest.java](./src/test/java/com/github/jsonldjava/clerezza/ClerezzaTripleCallbackTest.java) for example Usage.
