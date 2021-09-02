public class CSVParser {

    public static void main(String[] args){

        ArgParser parser = new ArgParser();
        if(parser.parse(args)){
            CSVFileIO csvFileIO = new CSVFileIO(parser.getInput(), parser.getOutput());
            if(csvFileIO.setQueryField(parser.getFields())){
                csvFileIO.process();
            }
        }

    }

}
