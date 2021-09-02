import java.io.*;
import java.security.PrivilegedExceptionAction;

public class CSVFileIO {

    private String[] queryFields;
    private File inputFile, outputFile;
    private Schema schema = new Schema();


    public CSVFileIO(File inputFile, File outputFile){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }


    public void process(){

        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            boolean headerWritten = false;
            writer = new BufferedWriter(new FileWriter(outputFile));
            reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while((line = reader.readLine()) != null && !schema.hasError()){
                if(schema.process(line)){
                    if(!headerWritten && schema.hasSchema()){
                        headerWritten = schema.writeHeader(writer, queryFields);
                    }
                    if(headerWritten){
                        schema.writeRows(writer, queryFields);
                    }
                }
            }
            if(schema.hasError()){
                System.out.println(schema.getErrorMsg());
            }else{
                schema.printSchema();
            }
        } catch (IOException e) {
            System.out.println("IOException : " + e.getMessage());
        }finally{
            try{
                if(writer != null)writer.close();
                if(reader != null)reader.close();
            } catch (IOException e){}
        }
    }


    /**
     *
     * @param queryFields
     * @return false if queryField has empty values
     */
    public boolean setQueryField(String[] queryFields) {
        this.queryFields = queryFields;
        if(queryFields != null){
            for(int i = 0; i < queryFields.length; i++){
                queryFields[i] = queryFields[i].strip();
                if(queryFields[i].isBlank()){
                    System.out.println("Specified query field is empty!!");
                    return false;
                }
            }
        }
        return true;
    }
}
