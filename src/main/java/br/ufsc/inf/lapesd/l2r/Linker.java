package br.ufsc.inf.lapesd.l2r;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;

public class Linker {

    public Model convertLiteralToResource(Model targetModel, Model... backgroundModel) {
        Model convertedModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        Map<String, Set<Resource>> mapLabelResourceUri = new HashMap<>();

        for (Model model : backgroundModel) {
            List<Resource> backgroundSubjects = model.listSubjects().toList();
            for (Resource resource : backgroundSubjects) {
                if (resource.hasProperty(RDFS.label)) {
                    String labelValue = resource.getProperty(RDFS.label).getObject().asLiteral().toString().toLowerCase();
                    Set<Resource> set = mapLabelResourceUri.get(labelValue);
                    if (set == null) {
                        mapLabelResourceUri.put(labelValue, set = new HashSet<Resource>());
                    }
                    set.add(resource);
                }
            }
        }

        List<Resource> targetSubjects = targetModel.listSubjects().toList();
        for (Resource resource : targetSubjects) {
            List<Statement> properties = resource.listProperties().toList();
            for (Statement statement : properties) {
                RDFNode object = statement.getObject();
                if (object.isLiteral()) {
                    String literalValue = object.asLiteral().toString().toLowerCase();
                    if (mapLabelResourceUri.get(literalValue) != null) {
                        List<Resource> backGroundResources = new ArrayList<>(mapLabelResourceUri.get(literalValue));
                        for (Resource backGroundResource : backGroundResources) {
                            convertedModel.add(statement.getSubject(), statement.getPredicate(), backGroundResource);
                        }
                    } else {
                        convertedModel.add(statement);
                    }
                } else {
                    convertedModel.add(statement);
                }
            }
        }

        return convertedModel;
    }
}
