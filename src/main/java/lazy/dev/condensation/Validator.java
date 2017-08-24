package lazy.dev.condensation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Validation class to check some assumptions about the schema.
 * Because json/bson/mongo is so flexible you can do things like:
 *   { "a" : "b" },{ "a": 1 }   (Same key different value type)
 *   [ 1, "a", {} ]             (One list many element types)
 * These don't translate well into statically typed languages like java so the validator will catch them.
 *
 * When checking lists, if one is null or empty and the other has elements validation will pass.
 */
public class Validator {

    private static Logger logger = Logger.getLogger("lazy.dev.condensation.Validator");
    private Map<Setting,Boolean> settings;

    public enum Conflict { MERGE_CONFLICT }

    public enum Setting {
        FAIL_FAST(true);
        boolean isOn;
        Setting(boolean isOn){
            this.isOn=isOn;
        }
        public static Map<Setting,Boolean> getDefaultSettings(){
            Map<Setting,Boolean> settings = new HashMap<>();
            for(Setting s : Setting.values()){
                settings.put(s,s.isOn);
            }
            return settings;
        }
    }

    /**
     * Default constructor
     */
    public Validator(){
        this.settings = Setting.getDefaultSettings();
    }

    /**
     * Override settings for this generator.
     * @param settings - the settings you want to override.
     */
    public Validator withSettings(Map<Setting,Boolean> settings){
        this.settings.putAll(settings);
        return this;
    }


    /** Helper class to make it clear a validation exception was raised */
    protected static class ValidationException extends Exception {
        private ValidationException(String msg){
            super(msg);
        }
    }

    /**
     * Fail the validation and proceed based on settings.
     * @param errorMessage - the error message to record for the failure.
     * @return false indicating the validation has failed.
     * @throws ValidationException - when {@link Setting.FAIL_FAST} is true.
     */
    protected boolean fail(String errorMessage) throws ValidationException {
        if(settings.get(Setting.FAIL_FAST)){
            throw new ValidationException(errorMessage);
        }
        logger.warning(errorMessage);
        return false;
    }
    /**
     * Check that the two objects are of the same type and if they are lists check that their elements are of the same type.
     * Validation is not recursive. It stops at the List's element type. Validation will pass if one list is empty or null.
     * @param key - the key corresponding to the objects.
     * @param o1 - the first object to validate.
     * @param o2 - the second object to validate against the first.
     * @return true if validation was successful, false otherwise.
     */
    protected boolean fuzzyValidate(String key, Object o1, Object o2) throws ValidationException {
        if(!this.haveSameClass(o1,o2)){
            return this.fail("Miss-matched types for key "+key+": "+o1.getClass()+":["+o1+"] != "+o2.getClass()+":["+o2+"]");
        }
        if(o1 instanceof List && !this.areCongruent((List) o1,(List) o2) ){
            return this.fail("Miss-matched types for key "+key+": "+"Lists o1 & o2 have elements of mismatched types!");
        }

        return true;
    }

    /**
     * Check if all the elements in the given Lists have the same type.
     * @param l1 - the first List.
     * @param l2 - the second ArrayList.
     * @return true if both lists are empty or they contain elements of the same type, false otherwise.
     */
    protected boolean areCongruent(List l1, List l2){
        if(l1.isEmpty() && l2.isEmpty()){
            return true; // Both of the lists are empty and therefore have the same type
        } else if(l1.isEmpty()){
            return this.isCongruent(l2);// Check the non empty list
        } else if(l2.isEmpty()){
            return this.isCongruent(l1);// Check the non empty list
        } else if(!this.isCongruent(l1) || !this.isCongruent(l2)){// Each list must have same type elements
            return false;
        }
        // At this point both lists have elements, and all the elements in each list individually have the same type.
        // All that is left to check is that both lists's elements have the same type.
        return this.haveSameClass(l1.get(0),l2.get(0));// Do the lists share the same type elements
    }

    /**
     * Check that every element in the list has the same type. (Not recursive)
     * @param l1 - the list to check.
     * @return true if every element has the same type, false otherwise.
     */
    protected boolean isCongruent(List l1){
        if(l1.isEmpty()){ return true; }

        Class<?> clazz = null;// Every element in both lists should be this type in order to return true
        boolean initialized = false;

        for(Object o : l1){
            if(!initialized){
                clazz = o==null? null: o.getClass();// Set clazz based on the first element, either null or the class
                initialized=true;
            } else {
                if(clazz==null){
                    if(o!=null){ return false;}// clazz is null but o is not
                    // clazz and o are both null
                } else {
                    if(o==null){ return false; }// o is null but clazz is not
                    if(!clazz.equals(o.getClass())){ return false; }// O's class is not clazz
                    // O's class is clazz
                }
                // Either both are null or O's class is clazz
            }
        }
        return true;
    }

    /**
     * Checks if two objects are of the same class not taking subclasses into consideration.
     * @param o1 - the first object.
     * @param o2 - the second object.
     * @return true if they have the same class or are both null, false otherwise.
     */
    protected boolean haveSameClass(Object o1, Object o2){
        if(o1==null && o2==null){ return true;  }   // Both are null
        if(o1==null || o2==null){ return false; }   // One is null but not both
        // Neither are null
        return o1.getClass().equals(o2.getClass());


    }

}
