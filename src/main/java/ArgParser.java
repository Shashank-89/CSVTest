import org.apache.commons.cli.*;

import java.io.File;

public class ArgParser {

    private File input, output;
    private String[] fields;

    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;


    public ArgParser(){
        super();
        options = new Options();
        options.addOption("i", "input", true, "input file path");
        options.addOption("o", "output", true, "output file path");
        options.addOption("f", "fields", true, "Fields to output separated by comma (,)");

        parser = new DefaultParser();
        formatter = new HelpFormatter();
    }


    public boolean parse(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);

            String inputArg = cmd.getOptionValue("i");
            String outputArg = cmd.getOptionValue("o");
            String fieldArg = cmd.getOptionValue("f");

            if(inputArg != null){
                input = new File(inputArg);
                if(!input.exists() || !input.isFile()){
                    System.out.println(input.getName() + " file does not exists!!");
                    formatter.printHelp("CSVParser", options);
                    return false;
                }
            }else{
                System.out.println("No input specified!!");
                formatter.printHelp("CSVParser", options);
                return false;
            }

            if(outputArg != null){
                if(inputArg.equals(outputArg)){
                    System.out.println("Input and output arguments can not be the same!!!");
                    formatter.printHelp("CSVParser", options);
                    return false;
                }
                output = new File(outputArg);
            } else{
                System.out.println("No Output specified!!");
                formatter.printHelp("CSVParser", options);
                return false;
            }

            if(fieldArg != null) fields = fieldArg.split(",");
            return true;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("CSVParser", options);
        }
        return false;
    }

    public File getOutput() {
        return output;
    }

    public File getInput(){
        return input;
    }

    public String[] getFields(){
        return fields;
    }
}
