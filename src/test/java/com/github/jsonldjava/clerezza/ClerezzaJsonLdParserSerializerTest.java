package com.github.jsonldjava.clerezza;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ServiceLoader;

import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.Language;
import org.apache.clerezza.commons.rdf.Triple;
import org.apache.clerezza.commons.rdf.impl.utils.PlainLiteralImpl;
import org.apache.clerezza.commons.rdf.impl.utils.TripleImpl;
import org.apache.clerezza.commons.rdf.impl.utils.simple.SimpleGraph;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClerezzaJsonLdParserSerializerTest {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    private static final Charset UTF8 = Charset.forName("UTF8");
    
    private static Graph rdfData;
    
    /**
     * Typical Clerezza Parser initialization. The JSON-LD serializing provider
     * will be found by using the java {@link ServiceLoader}
     */
    private Parser parser = Parser.getInstance();
    /**
     * Typical Clerezza Serializer initialization. The JSON-LD serializing provider
     * will be found by using the java {@link ServiceLoader}
     */
    private Serializer serializer = Serializer.getInstance();

    @BeforeClass
    public static void init(){
        LiteralFactory lf = LiteralFactory.getInstance();
        IRI pers1 = new IRI("http://www.example.org/test#pers1");
        IRI pers2 = new IRI("http://www.example.org/test#pers2");
        Graph data = new SimpleGraph();
        //NOTE: This test a language literal with and without language as
        //      well as a xsd:string typed literal. To test correct handling of
        //      RDF1.1
        data.add(new TripleImpl(pers1, RDF.type, FOAF.Person));
        data.add(new TripleImpl(pers1, FOAF.name, new PlainLiteralImpl("Rupert Westenthaler",
                new Language("de"))));
        data.add(new TripleImpl(pers1, FOAF.nick, new PlainLiteralImpl("westei")));
        data.add(new TripleImpl(pers1, FOAF.mbox, lf.createTypedLiteral("rwesten@apache.org")));
        data.add(new TripleImpl(pers1, FOAF.age, lf.createTypedLiteral(38)));
        data.add(new TripleImpl(pers1, FOAF.knows, pers2));
        data.add(new TripleImpl(pers2, FOAF.name, new PlainLiteralImpl("Reto Bachmann-Gmür")));
        rdfData = data;
    }
    
    @Test
    public void parserTest() {
        final InputStream in = getClass().getClassLoader().getResourceAsStream(
                "testfiles/product.jsonld");
        SimpleGraph graph = new SimpleGraph();
        parser.parse(graph, in, "application/ld+json");
        Assert.assertEquals(13, graph.size());
    }
    @Test
    public void serializerTest(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(out, rdfData, "application/ld+json");
        byte[] data = out.toByteArray();
        log.info("Serialized Graph: \n {}",new String(data,UTF8));
       
        //Now we reparse the graph to validate it was serialized correctly
        SimpleGraph reparsed = new SimpleGraph();
        parser.parse(reparsed, new ByteArrayInputStream(data), "application/ld+json");
        Assert.assertEquals(7, reparsed.size());
        for(Triple t : rdfData){
            Assert.assertTrue(reparsed.contains(t));
        }
        
    }
}
