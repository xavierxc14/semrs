package com.googlecode.semrs.util;

import java.io.PrintWriter;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * @author Roger Marin
 *
 * Clase Utilitaria utilizada como contenedor
 * del Modelo para ser manipulada desde la capa web.
 */

public class ModelContainer {
	
	private OntModel model;
	
	/**
	 * Contructor de la Clase Contenedora
	 * El modelo tiene acceso limitado de manera
	 * de que no pueda ser modificado
	 * @param model
	 */
	public ModelContainer(OntModel model){
		this.model = model;
	}
	
	/**
	 * Metodo que recibe un Objeto Printwriter y un Formato
	 * Para Serializar el modelo(Ontología) al formato especificado
	 * Formatos Posibles:
	 * N3
	 * N-TRIPLE
	 * RDF/XML
	 * RDF/XML-ABBREV
	 * 
	 * @param out
	 * @param format
	 */
	public void dumpModel(PrintWriter out, String format){
		this.model.write(out, format);			
	}
	
	

}
