package lazy.dev.condensation;

import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { PropertyPlaceholderAutoConfiguration.class, MongoConfig.class })
public class CondensationTests {

    private static Document document = null;

    @BeforeClass
    public static void setup() {
        document = new Document("key", "value");
        document.put("list1", Arrays.asList("A","B","C"));
        document.put("list2", Arrays.asList("A",new Document("B","D"),"C"));
        document.put("list3", Arrays.asList(1, new Date()));
        document.put("list4", Arrays.asList(null,null));
        document.put("list5", Arrays.asList(null,new ArrayList<>()));
        document.put("list6", Arrays.asList("X",null));
        document.put("list7", Arrays.asList(new Document(),new Document()));
        document.put("list8", Arrays.asList("X","Y","Z"));
        document.put("list9", Arrays.asList(new Document()));
        document.put("list10", Arrays.asList(new Document()));
        document.put("list11", Arrays.asList(1,2,3));
        document.put("number1", 1);
        document.put("number2", 2);
        document.put("nestedDocument1",new Document());
        document.put("nestedDocument2",new Document());
        document.put("date1",new Date());
        document.put("null",null);
    }

	@Test public void contextLoads() { }

//    @Test
//    public void testIsComplextType() {
//        assertTrue(Condensation.isComplextType(document));
//        assertTrue(Condensation.isComplextType(document.get("list1")));
//
//        assertFalse(Condensation.isComplextType(null));
//        assertFalse(Condensation.isComplextType(document.get("key")));
//        assertFalse(Condensation.isComplextType(document.get("number1")));
//    }
//
//    @Test
//    public void testIsComlextList(){
//        assertTrue(Condensation.isComplexList(document.get("list7")));
//
//        assertFalse(Condensation.isComplexList(null));
//        assertFalse(Condensation.isComplexList(""));
//        assertFalse(Condensation.isComplexList(new ArrayList<>()));
//        assertFalse(Condensation.isComplexList(document.get("list1")));
//    }
//
//    @Test
//    public void testIsList(){
//        assertTrue(Condensation.isList(document.get("list1")));
//        assertFalse(Condensation.isList(document.get("key")));
//        assertFalse(Condensation.isList(null));
//    }
//
//    @Test
//    public void testIsDocument(){
//        assertTrue(Condensation.isDocument(document));
//        assertTrue(Condensation.isDocument(document.get("nestedDocument1")));
//        assertFalse(Condensation.isDocument(document.get("list1")));
//        assertFalse(Condensation.isDocument(document.get("key")));
//        assertFalse(Condensation.isDocument(null));
//    }
//
//    @Test
//    public void testHaveSameType() {
//        assertTrue(Condensation.haveSameType(null,null));
//        assertTrue(Condensation.haveSameType(document.get("number1"),document.get("number2")));
//        assertTrue(Condensation.haveSameType(document.get("nestedDocument1"),document.get("nestedDocument2")));
//
//        assertFalse(Condensation.haveSameType(null,document));
//        assertFalse(Condensation.haveSameType(document.get("list1"),null));
//        assertFalse(Condensation.haveSameType(document.get("list1"),document.get("nestedDocument1")));
//        assertFalse(Condensation.haveSameType(document.get("list1"),document.get("number1")));
//        assertFalse(Condensation.haveSameType(document.get("date1"),document.get("nestedDocument1")));
//    }
//
//    @Test
//    public void testIsCongruent(){
//        assertTrue(Condensation.isCongruent(new ArrayList()));
//        assertTrue(Condensation.isCongruent((List) document.get("list1")));
//        assertTrue(Condensation.isCongruent((List) document.get("list4")));
//        assertTrue(Condensation.isCongruent((List) document.get("list7")));
//
//        assertFalse(Condensation.isCongruent((List) document.get("list2")));
//        assertFalse(Condensation.isCongruent((List) document.get("list3")));
//        assertFalse(Condensation.isCongruent((List) document.get("list5")));
//        assertFalse(Condensation.isCongruent((List) document.get("list6")));
//    }
//
//    @Test
//    public void testAreCongruent(){
//        assertTrue(Condensation.areCongruent(new ArrayList(),new ArrayList()));
//        assertTrue(Condensation.areCongruent(new ArrayList(),(List) document.get("list1")));
//        assertTrue(Condensation.areCongruent((List) document.get("list1"),new ArrayList()));
//        assertTrue(Condensation.areCongruent((List) document.get("list1"),(List) document.get("list8")));
//        assertTrue(Condensation.areCongruent((List) document.get("list9"),(List) document.get("list10")));
//
//        assertFalse(Condensation.areCongruent(new ArrayList(),(List) document.get("list2")));
//        assertFalse(Condensation.areCongruent((List) document.get("list2"),new ArrayList()));
//        assertFalse(Condensation.areCongruent((List) document.get("list2"),(List) document.get("list1")));
//        assertFalse(Condensation.areCongruent((List) document.get("list1"),(List) document.get("list9")));
//        assertFalse(Condensation.areCongruent((List) document.get("list1"),(List) document.get("list11")));
//    }

//    @Test
//    public void testAddMissingKeys(){
//        Document copy = new Document("list1",Arrays.asList("X","Y","Z"));
//        Condensation.addMissingKeys(copy,document);
//        assertTrue(copy.get("key")!=null);
//        assertTrue(copy.get("nestedDocument1").equals(new Document()));
//        assertTrue(copy.get("list1").equals(Arrays.asList("X","Y","Z")));// Key already exists, it should not be written over
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void testAddMissingKeys_Null(){
//        Document copy = null;
//        Condensation.addMissingKeys(null,document);
//    }



}
