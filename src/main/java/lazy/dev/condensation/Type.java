package lazy.dev.condensation;

import org.bson.Document;
import java.util.List;

/**
 * Internal type used for merging mongo bson documents.
 * Contains several convenience methods to express conditions in a human readable format.
 */
public enum Type {
    NULL, SIMPLE, DOCUMENT, EMPTY_LIST, SIMPLE_LIST, DOCUMENT_LIST, NESTED_LIST, NULL_LIST;

    /**
     * Convenience method for checking if the type is a document.
     * @return true if the type is a document false otherwise.
     */
    public boolean isDocument(){
        return this.equals(DOCUMENT);
    }

    /**
     * Convenience method for checking if the type is a list of documents.
     * @return true if the type is a list of documents false otherwise.
     */
    public boolean isDocumentList(){
        return this.equals(DOCUMENT_LIST);
    }

    /**
     * Convenience method for checking if the type is a list of lists.
     * @return true if the type is a list of lists false otherwise.
     */
    public boolean isNestedList(){
        return this.equals(NESTED_LIST);
    }

    /**
     * Convenience method for checking if the type is a list of simple types.
     * @return true if the type is a list of simple types false otherwise.
     */
    public boolean isSimpleList(){
        return this.equals(SIMPLE_LIST);
    }

    /**
     * Convenience method for checking if the type is a list.
     * @return true if the type is a list.
     */
    public boolean isList(){
        return this.equals(EMPTY_LIST) || this.equals(SIMPLE_LIST) || this.equals(DOCUMENT_LIST) || this.equals(NESTED_LIST) || this.equals(NULL_LIST);
    }

    /**
     * Get the type for the given objects that will be merged.
     * @param o1 - the first object.
     * @param o2 - the second object.
     * @return the type of the object, if one object is an empty list the other's type will be returned.
     */
    protected static Type getFuzzyType(Object o1, Object o2) {
        Type t1 = Type.getType(o1);
        Type t2 = Type.getType(o2);
        if (t1.equals(t2)) {
            return t1;
        }
        // Handle fuzzyValidation Criteria, If a one list is empty return the other's type
        if (t1.equals(EMPTY_LIST) && t2.isList()) {
            return t2;
        } else if (t2.equals(EMPTY_LIST) && t1.isList()) {
            return t1;
        }

        throw new IllegalStateException("Mismatched types: "+t1+","+t2+".");
    }

    /**
     * Get the type from an object.
     * Use {@link #getFuzzyType(Object, Object)} to satisfy fuzzyValidation criteria.
     * @param o - the object to inspect
     * @return the type of the object
     */
    private static Type getType(Object o){
        if(o==null){
            return NULL;
        }
        else if(o instanceof Document){
            return DOCUMENT;
        } else if(o instanceof List){
            return Type.getListType(o);
        }
        return SIMPLE;
    }

    /**
     * Ge the type of the list.
     * @param o - the object to inspect
     * @return the type of the list
     */
    private static Type getListType(Object o){
        if(((List) o).isEmpty()){
            return EMPTY_LIST;
        }

        for(Object element : (List) o){
            if(element!=null){

                if(element instanceof Document){
                    return DOCUMENT_LIST;
                } else if(element instanceof List){
                    return NESTED_LIST;
                }

                return SIMPLE_LIST;
            }
        }

        return NULL_LIST;
    }
}