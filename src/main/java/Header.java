import org.apache.commons.validator.routines.EmailValidator;

public class Header {

    private String name;
    private boolean isEmail;
    private Type type = Type.UNKNOWN;

    public Header(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public boolean validateType(String val){

        if(type == Type.UNKNOWN){
            if(!val.isBlank()) assignType(val);
            return true;
        }else{
            switch(type){

                case LONG:
                    boolean ret = isLong(val);
                    if(!ret && (ret = isDouble(val))) {
                        type = Type.DOUBLE;
                    }
                    return ret;

                case DOUBLE:
                    return isDouble(val);

                case BOOLEAN:
                    return isBoolean(val);

                case STRING:
                default:
                    if(isEmail){
                        return validateEmail(val);
                    }
                    return true;
            }
        }
    }

    public void assignType(String val) {
        if(isLong(val)){
            type = Type.LONG;
        }else if(isDouble(val)){
            type = Type.DOUBLE;
        }else if(isBoolean(val)){
            type = Type.BOOLEAN;
        }else {
            type = Type.STRING;
            isEmail = validateEmail(val);
        }
    }

    private boolean validateEmail(String email){
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    private boolean isDouble(String val) {
        try{
            Double.parseDouble(val);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    private boolean isLong(String val){
        try{
            Long.parseLong(val);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public boolean isEmail(){
        return isEmail;
    }

    private boolean isBoolean(String val){
        return "true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val);
    }

    public String getType() {
        return type.name();
    }

    enum Type{
        BOOLEAN, LONG, DOUBLE, STRING, UNKNOWN
    }
}
