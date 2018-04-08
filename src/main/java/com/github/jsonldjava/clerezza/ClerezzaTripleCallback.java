package com.github.jsonldjava.clerezza;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.clerezza.commons.rdf.BlankNode;
import org.apache.clerezza.commons.rdf.BlankNodeOrIRI;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.Language;
import org.apache.clerezza.commons.rdf.RDFTerm;
import org.apache.clerezza.commons.rdf.impl.utils.PlainLiteralImpl;
import org.apache.clerezza.commons.rdf.impl.utils.TripleImpl;
import org.apache.clerezza.commons.rdf.impl.utils.TypedLiteralImpl;
import org.apache.clerezza.commons.rdf.impl.utils.simple.SimpleGraph;

import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;

public class ClerezzaTripleCallback implements JsonLdTripleCallback {

    private static final String RDF_LANG_STRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";

    private Graph mGraph = new SimpleGraph();
    private Map<String, BlankNode> bNodeMap = new HashMap<>();

    public void setMGraph(Graph graph) {
        this.mGraph = graph;
        bNodeMap = new HashMap<>();
    }

    public Graph getMGraph() {
        return mGraph;
    }

    private void triple(String s, String p, String o, String graph) {
        if (s == null || p == null || o == null) {
            // TODO: i don't know what to do here!!!!
            return;
        }

        final BlankNodeOrIRI subject = getNonLiteral(s);
        final IRI predicate = new IRI(p);
        final BlankNodeOrIRI object = getNonLiteral(o);
        mGraph.add(new TripleImpl(subject, predicate, object));
    }

    private void triple(String s, String p, String value, String datatype, String language,
            String graph) {
        final BlankNodeOrIRI subject = getNonLiteral(s);
        final IRI predicate = new IRI(p);
        RDFTerm object;
        if (language != null) {
            object = new PlainLiteralImpl(value, new Language(language));
        } else if (datatype == null || RDF_LANG_STRING.equals(datatype)) {
            object = new PlainLiteralImpl(value);
        } else {
            object = new TypedLiteralImpl(value, new IRI(datatype));
        }

        mGraph.add(new TripleImpl(subject, predicate, object));
    }

    private BlankNodeOrIRI getNonLiteral(String s) {
        if (s.startsWith("_:")) {
            return getBNode(s);
        } else {
            return new IRI(s);
        }
    }

    private BlankNode getBNode(String s) {
        if (bNodeMap.containsKey(s)) {
            return bNodeMap.get(s);
        } else {
            final BlankNode result = new BlankNode();
            bNodeMap.put(s, result);
            return result;
        }
    }

    @Override
    public Object call(RDFDataset dataset) {
        for (String graphName : dataset.graphNames()) {
            final List<RDFDataset.Quad> quads = dataset.getQuads(graphName);
            if ("@default".equals(graphName)) {
                graphName = null;
            }
            for (final RDFDataset.Quad quad : quads) {
                if (quad.getObject().isLiteral()) {
                    triple(quad.getSubject().getValue(), quad.getPredicate().getValue(), quad
                            .getObject().getValue(), quad.getObject().getDatatype(), quad
                            .getObject().getLanguage(), graphName);
                } else {
                    triple(quad.getSubject().getValue(), quad.getPredicate().getValue(), quad
                            .getObject().getValue(), graphName);
                }
            }
        }

        return getMGraph();
    }

}
