package com.googlecode.dtools.typeinfo;

import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;
import com.googlecode.dtools.typeinfo.util.SpringUtil;
import com.googlecode.dtools.typeinfo.work.Worker;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 10.06.11
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static boolean justPrintUsage = false;
    private static String outputDir;
    private static String singleDmType;
    private static boolean listFromConfig = false;
    private static boolean justTestConnection = false;
    private static boolean justPrintVersion = false;

    public static void main(String[] args) throws Exception {
        collectArgs(args);
        if (justPrintUsage) {
            printUsage();
            return;
        }
        if (justPrintVersion) {
            printVersion();
            return;
        }
        Worker worker = (Worker) SpringUtil.getSpringBean("worker");
        if (outputDir != null) {
            modifyWorkerConfig(worker);
        }
        if (justTestConnection) {
            testConnection(worker);
            return;
        } if (singleDmType!=null){
            worker.processSingle(singleDmType,null);
            return;
        }
       /* List<String> list = (List<String>)SpringUtil.getSpringBean("typesList");
        worker.processList(list);*/
        
        if (listFromConfig){
            List<String> list = (List<String>)SpringUtil.getSpringBean("typesList");
            worker.processList(list);
        } else
            worker.work();
        
    }

    private static void modifyWorkerConfig(Worker worker) {
        worker.getFilesWorker().setOutputPath(outputDir);
        //To change body of created methods use File | Settings | File Templates.
    }

    private static void printVersion() {
        System.out.println("TODO Not implemented");
    }

     private static void testConnection(Worker worker) throws Exception {
         DmTypeDAO dao =  worker.getDmTypeDAO();
        if (dao.testConnection()) {
            System.out.println("OK success connection to docbase " + dao.getDocbase());
        } else {
            System.out.println("FAIL connect to " + dao.getDocbase());
        }
    }

    private static void collectArgs(String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-help") || arg.equals("-h")) {
                justPrintUsage = true;
            } else if (arg.equals("-o") || arg.equals("-output")) {
                outputDir = args[i + 1];
                i++;
                createDir();
            } else if (arg.equals("-tc") || arg.equals("--test-connection")) {
                justTestConnection = true;
            } else if (arg.equals("-single")) {
                singleDmType = args[i+1];
                i++;
            } else if (arg.equals("-version")) {
                justPrintVersion = true;
            } else if (arg.equals("-list")) {
                listFromConfig = true;
            }else if (arg.startsWith("-")) {
                // we don't have any more args to recognize!
                String msg = "Unknown argument: " + arg;
                System.err.println(msg);
                printUsage();
                throw new Exception("");
            }
        }
    }

    private static void createDir() throws Exception {
        if (!outputDir.endsWith("\\")) {
            outputDir += "\\";
        }
        try {
            File dir = new File(outputDir);
            if (!dir.exists()) {
                // Create directory
                boolean success = dir.mkdir();
                if (success) {
                    System.out.println("Directory: "
                            + outputDir + " created");
                }
            }
        } catch (Exception e) {
            System.err.println("Cannot create output directory with name, exit");
            throw new Exception("");
        }
    }

    /**
     * Prints the usage information for this class to <code>System.out</code>.
     */
    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("typeinfo [-o]" + lSep);
        msg.append("util for export documentum type to xml" + lSep);
        msg.append("more config options in typeinfo.config.xml " + lSep);
        msg.append("Options: " + lSep);
        msg.append("  -help, -h              print this message" + lSep);
        msg.append("  -output, -o            output dir" + lSep);
        msg.append("  -list                  process list from typeinfo.config.xml" + lSep);
        msg.append("  -single [name]         process single dm_type" + lSep);
        msg.append("  --test-connection,-tc  test documentum connection available" + lSep);
        msg.append("  -version,              print version info and exit" + lSep);
        System.out.println(msg.toString());
    }
}
