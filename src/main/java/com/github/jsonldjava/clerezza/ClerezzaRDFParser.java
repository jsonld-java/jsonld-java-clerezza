package com.github.jsonldjava.clerezza;

import java.util.HashMap;
import java.util.Map;

import org.apache.clerezza.commons.rdf.BlankNode;
import org.apache.clerezza.commons.rdf.BlankNodeOrIRI;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.commons.rdf.Literal;
import org.apache.clerezza.commons.rdf.RDFTerm;
import org.apache.clerezza.commons.rdf.Triple;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFParser;

/**
 * Converts a Clerezza {@link Graph} to the {@link RDFDataset} used
 * by the {@link JsonLdProcessor}
 * 
 * @author Rupert Westenthaler
 * 
 */
public class ClerezzaRDFParser implements RDFParser {

    private static String RDF_LANG_STRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";

    private long count = 0;

    @Override
    public RDFDataset parse(Object input) throws JsonLdError {
        count = 0;
        final Map<BlankNode, String> blankNodeMap = new HashMap<BlankNode, String>(1024);
        final RDFDataset result = new RDFDataset();
        if (input instanceof Graph) {
            for (final Triple t : ((Graph) input)) {
                handleStatement(result, t, blankNodeMap);
            }
        }
        blankNodeMap.clear(); // help gc
        return result;
    }

    private void handleStatement(RDFDataset result, Triple t, Map<BlankNode, String> blankNodeMap) {
        final String subject = getResourceValue(t.getSubject(), blankNodeMap);
        final String predicate = getResourceValue(t.getPredicate(), blankNodeMap);
        final RDFTerm object = t.getObject();

        if (object instanceof Literal) {
        	final Literal literalObject = (Literal) object;
            final String value = literalObject.getLexicalForm();
            final String language;
            final String datatype;
            if (literalObject.getLanguage() != null) {
            	language = literalObject.getLanguage().toString();
            	datatype = RDF_LANG_STRING;
            } else {
                datatype = getResourceValue(literalObject.getDataType(), blankNodeMap);
                language = null;
            }
            result.addTriple(subject, predicate, value, datatype, language);
            count++;
        } else {
            result.addTriple(subject, predicate, getResourceValue((BlankNodeOrIRI) object, blankNodeMap));
            count++;
        }

    }

    /**
     * The count of processed triples (not thread save)
     * 
     * @return the count of triples processed by the last {@link #parse(Object)}
     *         call
     */
    public long getCount() {
        return count;
    }

    private String getResourceValue(BlankNodeOrIRI nl, Map<BlankNode, String> BlankNodeMap) {
        if (nl == null) {
            return null;
        } else if (nl instanceof IRI) {
            return ((IRI) nl).getUnicodeString();
        } else if (nl instanceof BlankNode) {
            String BlankNodeId = BlankNodeMap.get(nl);
            if (BlankNodeId == null) {
                BlankNodeId = Integer.toString(BlankNodeMap.size());
                BlankNodeMap.put((BlankNode) nl, BlankNodeId);
            }
            return new StringBuilder("_:b").append(BlankNodeId).toString();
        } else {
            throw new IllegalStateException("Unknwon BlankNodeOrIRI type " + nl.getClass().getName()
                    + "!");
        }
    }
}

