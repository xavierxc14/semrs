package com.googlecode.semrs.util.sparql.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.googlecode.semrs.util.Util;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;
import com.hp.hpl.jena.sparql.util.FmtUtils;

/**
 * @author Roger Marin
 *
 * Extensión de ARQ-SPARQL para el calculo de una edad en base a una fecha de nacimiento
 */
public class Age extends FunctionBase1{

	private static final Logger LOG = Logger.getLogger(Age.class);
 
	public NodeValue exec(NodeValue v) {

        	Node n = v.asNode() ;
            if ( ! n.isLiteral() )
                throw new ExprEvalException("Not a Literal: "+FmtUtils.stringForNode(n)) ;
            String str = n.getLiteralLexicalForm();
            LOG.debug("Calling getAge with date = " + str);
        	SimpleDateFormat xsdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        	int age = 0;
        	try {
				Date dob = xsdFormat.parse(str);
				age = Util.getAge(dob);
			} catch (ParseException e) {
				LOG.error(e);
			}
        	
        	return NodeValue.makeInteger(String.valueOf(age));
        
	} 
	
}
