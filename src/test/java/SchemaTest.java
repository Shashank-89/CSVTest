import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Assumptions:
 * Header name can be anything, so not relying on any specific field like email etc from header.
 * Header line cannot have any empty field, however rows can have empty values
 * Rows can have empty values (but should have same number of fields still)
 * If rows have problems then they are ignored
 * Type is checked keeping empty values in mind, so if the initial rows have empty values then the type
 * is unknown until a value is encountered (after this point typ is strictly checked for subsequent rows)
 * Email field is determined in the same manner as above
 * If type check on a row fails or email is not present then it is ignored
 */
public class SchemaTest {

    ArrayList<String[]> headerTestInputs = new ArrayList<>();
    ArrayList<String[]> headerFailedInputs = new ArrayList<>();

    public SchemaTest(){

        headerTestInputs.add(new String[]{"a,b,c,d,e",
                "1,True,email@domain.com,,",
                "2,False,email|domain.com,string,5.0",
                "3,False,email@domain.com,string,5.0",
                "4,False,,email@domain.com,5.0",
                ",email@domain.com,,,",
                "3.4,,email@domain.com,,"});

        headerFailedInputs.add(new String[]{"a,b,c,d,e", "1,email@domain.com,,",
                "a,b,,d,e", "2,email@domain.com,,3,"});
    }

    @Test
    void testSchemaDetection(){

        Schema schema;
        for(String[] headInp : headerTestInputs){
            schema = new Schema();
            for(String line : headInp){
                System.out.println(schema.process(line));
            }
            Assertions.assertTrue(schema.hasSchema());
            schema.printSchema();
            //writeOutput(schema, null);
            writeOutput(schema, new String[]{"a","c"});
        }

        for(String[] headInp : headerFailedInputs){
            schema = new Schema();
            for(String line : headInp){
                System.out.println(schema.process(line));
            }
            Assertions.assertFalse(schema.hasSchema());
            schema.printSchema();
            writeOutput(schema, null);
        }
    }


    private void writeOutput(Schema schema, String[] queryFields){
        PrintWriter writer = null;
        try{
            writer = new PrintWriter(System.out);
            if(schema.hasSchema()) schema.writeHeader(writer, queryFields);
            while(schema.writeRows(writer, queryFields));
        } catch (IOException e) {

        } finally {
            if(writer != null) writer.close();
        }
    }



}
