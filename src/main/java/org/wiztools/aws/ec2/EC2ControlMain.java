package org.wiztools.aws.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 *
 * @author subwiz
 */
public class EC2ControlMain {
    
    private static final String CMD_START = "start";
    private static final String CMD_STOP = "stop";
    private static final String CMD_TERMINATE = "terminate";
    
    private static final int EXIT_CLI_ERROR = 1;
    private static final int EXIT_IO_ERROR = 2;
    private static final int EXIT_EC2_ERROR = 3;
    private static final int EXIT_SYS_ERROR = 4;
    
    private static void printCommandLineHelp(PrintStream out){
        out.println("Usage: ec2-control [options] <instances>");
        out.println("Where options are:");
        
        String opts =
                "  -a  AWS access key (not needed when -k option is used).\n" +
                "  -s  AWS secret key (not needed when -k option is used).\n" +
                "  -k  Java properties file with AWS credentials.\n" +
                "  -r  AWS EC2 region (defaults to `us-east-1').\n" +
                "  -c  Command. Can be either `start' or `stop'.\n" +
                "  -h  Prints this help.\n";
        
        out.println(opts);
        
        String moreHelp = "Format of `aws-creds-file': \n"
                + "\tAWSAccessKeyId=XXX\n"
                + "\tAWSSecretKey=XXX";
        
        out.println(moreHelp);
    }
    
    public static void main(String[] args) {
        try {
            OptionParser parser = new OptionParser( "a:s:k:r:c:h" );
            OptionSet options = parser.parse(args);
            
            if(options.has("h")) {
                printCommandLineHelp(System.out);
                System.exit(0);
            }
            
            String awsCredsFile = (String) options.valueOf("k");
            String accessKey = (String) options.valueOf("a");
            String secretKey = (String) options.valueOf("s");
            String command = (String) options.valueOf("c");
            String region = (String) options.valueOf("r");
            
            if(awsCredsFile == null && (accessKey == null || secretKey == null)) {
                System.err.println("Either -k or (-a and -s) options are mandatory.");
                printCommandLineHelp(System.err);
                System.exit(EXIT_CLI_ERROR);
            }
            
            if(awsCredsFile != null && (accessKey != null && secretKey != null)) {
                System.err.println("Options -k and (-a and -s) cannot coexist.");
                printCommandLineHelp(System.err);
                System.exit(EXIT_CLI_ERROR);
            }
            
            if(command == null ||
                    !(command.equals(CMD_START) || command.equals(CMD_STOP) || command.equals(CMD_TERMINATE))) {
                System.err.println("Valid commands are `start', `stop' or `terminate'. You supplied: " + command);
                printCommandLineHelp(System.err);
                System.exit(EXIT_CLI_ERROR);
            }
            
            if(awsCredsFile != null) {
                Properties p = new Properties();
                try {
                    p.load(new FileInputStream(new File(awsCredsFile)));
                    
                    accessKey = p.getProperty("AWSAccessKeyId");
                    secretKey = p.getProperty("AWSSecretKey");
                }
                catch(IOException ex) {
                    System.err.println("Cannot read AWS property file.");
                    ex.printStackTrace(System.err);
                    System.exit(EXIT_IO_ERROR);
                }
            }
            
            AWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            AmazonEC2 client = new AmazonEC2Client(awsCreds);
            
            if(region != null && !region.trim().equals("")) {
                client.setRegion(Region.getRegion(Regions.fromName(region)));
            }
            
            // Loop through all ec2 instances:
            List<String> instances = new ArrayList<>();
            for(Object o: options.nonOptionArguments()) {
                instances.add((String) o);
            }
            
            EC2Control processor = new EC2Control(client, instances);
            
            switch (command) {
                case CMD_START:
                    processor.start();
                    break;
                case CMD_STOP:
                    processor.stop();
                    break;
                case CMD_TERMINATE:
                    processor.terminate();
                    break;
            }
        }
        catch(OptionException ex) {
            System.err.println(ex.getMessage());
            printCommandLineHelp(System.err);
            System.exit(EXIT_CLI_ERROR);
        }
    }
}
