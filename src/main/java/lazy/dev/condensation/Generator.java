package lazy.dev.condensation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import static lazy.dev.condensation.Validator.ValidationException;

import java.util.*;
import java.util.logging.Logger;


/**
 * The heart of the application. Processes a {@link MongoCollection} to generate a {@link Document} chema.
 */
public class Generator {

    private static Logger logger = Logger.getLogger("lazy.dev.condensation.Generator");

    private String collectionName;
    private Map<Setting,Boolean> settings;
    private MongoCollection mongoCollection;
    private Validator validator;

    /**
     * Settings for this Generator.
     */
    public enum Setting {
        MARK_CONFLICTS(true),
        TRUNCATE_LISTS(true);
        boolean isOn;
        Setting(boolean isOn){
            this.isOn=isOn;
        }
        public static Map<Setting,Boolean> getDefaultSettings(){
            Map<Setting,Boolean> settings = new HashMap<>();
            for(Setting s :Setting.values()){
                settings.put(s,s.isOn);
            }
            return settings;
        }
    }

    /**
     * Default constructor
     */
    public Generator(){
        this.settings = Setting.getDefaultSettings();
    }

    /**
     * Create a generator for a specific {@link MongoCollection}
     * @param mongoCollection - the mongo collection.
     * @return the generator.
     */
    public Generator forCollection(MongoCollection mongoCollection){
        this.mongoCollection = mongoCollection;
        this.collectionName = mongoCollection.getNamespace().getCollectionName();
        return this;
    }

    /**
     * Override settings for this generator.
     * @param settings - the settings you want to override.
     * @return the generator.
     */
    public Generator withSettings(Map<Setting,Boolean> settings){
        this.settings.putAll(settings);
        return this;
    }

    /**
     * Set the validator for this generator.
     * @param validator - the validator to use for this generator.
     * @return the generator.
     */
    public Generator withValidator(Validator validator){
        this.validator=validator;
        return this;
    }

    /**
     * Entry point into the Generator. Takes a mongo collection and creates a single document schema.
     * @return the document schema.
     */
    public Document generateSchema(){
        return this.generateSchema(this.getDocumentSet());
    }

    /**
     * Get a set of unique documents in this collection.
     */
    protected Set<Document> getDocumentSet(){
        collectionName = mongoCollection.getNamespace().getCollectionName();
        logger.info("Processing collection: "+ collectionName);

        MongoCursor cursor = mongoCollection.find().iterator();
        Set<Document> documentSet = new HashSet<>();

        while(cursor.hasNext()){
            Document doc = (Document) cursor.next();

            if(!documentSet.contains(doc)){
                documentSet.add(doc);
            }
        }

        logger.info("["+documentSet.size()+"] unique "+ collectionName +" documents found.");

        return documentSet;
    }

    /**
     * Builds a document for a document set. The resulting document will contain every unique key,
     * including nested keys, found in the document set.
     * @param documentSet - the document set to process.
     * @return - the document schema.
     */
    protected Document generateSchema(Set<Document> documentSet){
        logger.info("Generating schema for: "+ collectionName);

        Iterator<Document> iterator = documentSet.iterator();
        Document schema = null;
        String documentType = "conditional";

        while(iterator.hasNext()){
            if(schema==null) {
                Document doc = iterator.next();
                String docType = doc.getString("componentType");

                // initialize schema
                if(documentType.equals(docType)){
                    schema = doc;
                }
//                schema = iterator.next();
            } else {
                Document doc = iterator.next();
                String docType = doc.getString("componentType");

                // same doc type different schema
                if(documentType.equals(docType) && !schema.equals(doc)){
                    logger.info("Found a: "+docType);
                    schema = this.mergeDocuments(schema,doc);
                }
            }
        }

        return schema;
    }

    /**
     * Given two documents combine them and return the result.
     * The new document will contain a set of all the keys in both documents.
     * @param d1 - the first document.
     * @param d2 - the second document.
     * @return - the combined document.
     */
    protected Document mergeDocuments(Document d1, Document d2){
        if(d1==null && d2==null){
            return null; // Both are null so their combination is null
        } else if(d1==null){
            return d2;  // One is null the other is not so their combination is the non null document
        } else if(d2==null){
            return d1;
        }
        // Neither Document is null

        if(d1.equals(d2)){ // If the documents are equal their combination is the same as one of the originals
            return d1;
        }
        // The documents are not null and not equal

        d1 = this.mergeExistingKeys(d1,d2);
        d1 = this.addMissingKeys(d1,d2);

        return d1;
    }

    /**
     * Get the first element of the list and return that in a list.
     * @param list - the list to process
     * @return the first element of the list in a list.
     */
    protected List truncateList(List list){
        if(list==null || list.isEmpty()){
            return list;
        }

        return (settings.get(Setting.TRUNCATE_LISTS))? this.newList(list.get(0)) : list;
    }


    /** [ [], [], [] ] => */
    protected List<List> mergeNestedList(List<List> lists){
        // TODO
        throw new UnsupportedOperationException("Nested lists are not yet supported.");
//        if(lists==null || lists.isEmpty()){
//            return lists;
//        }
//        List nestedList = lists.get(0);
//        Type nestedType = Type.getType(nestedList);
//        if(nestedType.isDocument()){// [ [{}], [{}], [{}] ]
//
//
//            for(Object element : nestedList){}
//
//            this.mergeDocumentList(nestedList)
//
//        } else if(nestedType.isDocumentList()){
//
//        } else if(nestedType.isNestedList()) {
//
//        }
        // Simple List
    }

    protected List<List> mergeNestedLists(List<List> lists1, List<List> lists2){
        // TODO actually build logic for this? or fail at Validation
        throw new UnsupportedOperationException("Nested lists are not yet supported.");
    }


    /** [{},{},{}] => [{}] **/
    protected List<Document> mergeDocumentList(List<Document> list){
        if(list==null || list.isEmpty()){
            return list;
        }
        Document merged = null;
        for(Document doc : list){
            merged = this.mergeDocuments(merged,doc);
        }
        return this.newList(merged);
    }

    /** [{},{},{}] x2 => [{}] **/
    protected List<Document> mergeDocumentLists(List<Document> l1, List<Document> l2){
        if(l1==null && l2==null){
            return null;
        } else if(l1==null){
            return this.mergeDocumentList(l2);
        } else if(l2==null){
            return this.mergeDocumentList(l1);
        }

        l1 = this.mergeDocumentList(l1);
        l2 = this.mergeDocumentList(l2);

        if(l1.isEmpty() && l2.isEmpty()){
            return l1;
        } else if(l1.isEmpty()){
            return l2;
        } else if(l2.isEmpty()){
            return l1;
        }

        return this.newList(this.mergeDocuments(l1.get(0),l2.get(0)));
    }


    /**
     * Evaluate the specified objects for known conflicts.
     * @param o1 - the first object.
     * @param o2 - the second object.
     * @return a list containing the conflict or an empty list if no conflicts where found.
     */
    protected List<Object> skipKnownConflicts(Object o1, Object o2){
        List<Object> conflicts = new ArrayList<>();
        if(o1 instanceof String && o1.equals(Validator.Conflict.MERGE_CONFLICT.name())) {
            conflicts.add(o1);
        } else if (o2 instanceof String && o2.equals(Validator.Conflict.MERGE_CONFLICT.name())){
            conflicts.add(o2);
        }
        return conflicts;
    }

    /**
     * Merge a single key in a document.
     * @param key - the key to merge.
     * @param o1 - the first object to merge.
     * @param o2 - the second object to merge.
     * @return the merged object.
     */
    protected Object mergeKey(String key, Object o1, Object o2) {
        if(o1==null && o2==null){
            return null;
        } else if(o1==null){
            return o2;
        } else if(o2==null){
            return o1;
        }

        if(o1.equals(o2)){
            return o1;// If o1 == o2 there is no need to merge.
            //TODO if you add other conflict types modify this line to use .valueOf or custom method
        } else if(settings.get(Setting.MARK_CONFLICTS)) {
            List<Object> conflicts = this.skipKnownConflicts(o1,o2);
            if(!conflicts.isEmpty()){
                return conflicts.get(0);// Found a conflict, return it without further processing.
            }
        }
        Type o1Type = null;

        try {
            if(validator.fuzzyValidate(key, o1, o2)){
                o1Type = Type.getFuzzyType(o1, o2);

                if(o1Type.isDocument()){ // Nested Document
                    return this.mergeDocuments((Document) o1,(Document) o2);// Recursion
                } else if(o1Type.isDocumentList()){ // A list of documents
                    return this.mergeDocumentLists((List<Document>) o1, (List<Document>) o2);
                } else if(o1Type.isNestedList()){// List of lists
                    return this.mergeNestedLists((List<List>) o1,(List<List>) o2);
                } else if(o1Type.isSimpleList()){
                    return this.truncateList((List) o1);// List of simple types
                }
                // If it is a simple type no merging is necessary
            } else if(settings.get(Setting.MARK_CONFLICTS)){
                return Validator.Conflict.MERGE_CONFLICT.name();
            }
        } catch(ValidationException validationException){
            // TODO for some reason logging / system.out are magically disabled here...
            String errorMsg = "Unable to merge key, cannot combine key: " + key + " " +
                    "\n" + o1.getClass() + ":" + o1 + "" +
                    "\n" + o2.getClass() + ":" + o2;
            throw new RuntimeException(errorMsg, validationException);
        }

        return o1;
    }

    /**
     * For the given documents merge the top level keys that exist in both.
     * @param d1 - the first document.
     * @param d2 - the second document.
     * @return the resulting merged document.
     */
    protected Document mergeExistingKeys(Document d1, Document d2){
        if(d1==null || d2==null){
            throw new NullPointerException("Document is null.");
        }

        for(String key: d2.keySet()){
            if(d1.containsKey(key)) {
                Object o1 = d1.get(key);
                Object o2 = d2.get(key);
                d1.put(key,this.mergeKey(key,o1,o2));// Put the results back in d1 with key
            }
        }

        return d1;
    }

    /**
     * Take all the top level keys of the second document that don't exist in the first and add them to the first.
     * It is expected that neither document is null.
     * @param d1 - the first document.
     * @param d2 - the second document.
     * @return - the first document with the second's keys added to it.
     */
    protected Document addMissingKeys(Document d1, Document d2) {
        if(d1==null || d2==null){
            throw new NullPointerException("Document is null.");
        }
        d2.keySet().iterator().forEachRemaining(key->d1.putIfAbsent(key,d2.get(key)));
        return d1;
    }

    /**
     * Convenience method to create an untyped ArrayList and add one element to it.
     * Use instead of Arrays.asList which has a different class as ArrayList!
     * @param o - the object to add to the list.
     * @return the new ArrayList
     */
    protected ArrayList newList(Object o){
        ArrayList list = new ArrayList();
        list.add(o);
        return list;
    }

}