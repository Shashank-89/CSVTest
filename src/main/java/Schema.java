import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Schema {

    private Map<String,Header> headers;

    private String[] queryFields;
    private String headerLine, row1, delim, errMsg;
    private boolean schemaCreated, containsEmail, error;

    private LinkedList<Map<String, String>> parsedQueue = new LinkedList<>();

    public boolean hasError(){
        return error;
    }

    public String getErrorMsg(){
        return errMsg;
    }

    public boolean process(String line) {
        if(headerLine == null){
            headerLine = line;
            return true;
        }else if(row1 == null){
            row1 = line;
        }
        if(!schemaCreated){
            createSchema();
            error = !schemaCreated;
            if(error){
                errMsg = "Failed to detect schema tried with (;,|) delimiters!!";
                return false;
            }
        }
        Map<String, String> row = parseRecord(line);
        if(row != null && containsEmail){
            parsedQueue.offer(row);
        }
        return schemaCreated;
    }


    public boolean writeHeader(Writer writer, String[] queryFields) throws IOException {

        StringBuilder builder = new StringBuilder();
        if(queryFields != null){
            for(int i = 0; i < queryFields.length; i++){
                if(headers.containsKey(queryFields[i])){
                    if(i > 0) builder.append(delim);
                    builder.append(queryFields[i]);
                }else{
                    error = true;
                    errMsg = "Specified Query field not found or is empty!!";
                    return false;
                }
            }
        }else{
            //writing all fields
            int i = 0;
            for(String key : headers.keySet()){
                if(i > 0) builder.append(delim);
                builder.append(key);
                i++;
            }
        }
        if(!error){
            builder.append("\n");
            writer.write(builder.toString());
            writer.flush();
        }
        return true;
    }


    public boolean writeRows(Writer writer, String[] queryFields) throws IOException {

        if(parsedQueue.peek() != null){

            Map<String, String> row = parsedQueue.poll();
            StringBuilder builder = new StringBuilder();
            if(queryFields != null){
                for(int i = 0; i < queryFields.length; i++){
                    if(i > 0) builder.append(delim);
                    builder.append(row.get(queryFields[i]));
                }
            }else{
                int i = 0;
                for(String key : row.keySet()){
                    if(i > 0) builder.append(delim);
                    builder.append(row.get(key));
                    i++;
                }
            }
            builder.append("\n");
            writer.write(builder.toString());
            writer.flush();
            return true;
        }
        return false;
    }


    public void printSchema(){
        if(schemaCreated){
            JSONObject schemaJson = new JSONObject();
            for(String key : headers.keySet()){
                schemaJson.put(key, headers.get(key).getType());
            }
            System.out.println(schemaJson.toString(2));
        }
    }


    private Map<String, String> parseRecord(String line){

        HashMap<String, String> map = null;
        String[] vals = line.split(delim, -1);

        if(vals.length == headers.size()){
            Header head;
            map = new HashMap<String,String>();

            int i = 0;
            for(String key : headers.keySet()){
                head = headers.get(key);
                vals[i] = vals[i].strip();
                if(head.validateType(vals[i])){
                    map.put(head.getName(), vals[i]);
                }else{
                    return null;
                }
                containsEmail = containsEmail || head.isEmail();
                i++;
            }
        }
        return map;
    }


    private boolean createSchema(){
        //assuming header will not have empty value
        String[] delims = {",","|",";"};
        String[] headers, vals;

        for(String delim : delims){
            if(headerLine.contains(delim)){
                headers = headerLine.split(delim);
                if(validateHeader(headers)){
                    vals = row1.split(delim, -1);
                    if(vals.length == headers.length){
                        this.delim = delim;
                        this.headers = createHeaders(headers);
                        return (schemaCreated = true);
                    }
                }
            }
        }
        return false;
    }


    private Map<String, Header> createHeaders(String[] headers){
        Map<String, Header> headerMap = new LinkedHashMap<>();
        for (String header : headers) {
            Header head = new Header(header.strip());
            headerMap.put(head.getName(), head);
        }
        return headerMap;
    }


    private boolean validateHeader(String[] headers){
        for (String head : headers){
            if(head.isBlank()) return false;
        }
        return headers.length > 0;
    }


    public boolean hasSchema(){
        return schemaCreated;
    }
}
