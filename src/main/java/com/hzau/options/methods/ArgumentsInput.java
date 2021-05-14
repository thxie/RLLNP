package com.hzau.options.methods;

import cn.hutool.core.io.FileUtil;
import com.hzau.Constant;
import com.hzau.options.domain.Arguments;
import com.hzau.Utils;
import org.apache.commons.cli.*;

public class ArgumentsInput {

    private static Options OPTIONS = new Options();
    private static final String OPT_H = "h";
    private static final String OPT_Help = "help";
    private static final String OPT_V = "v";
    private static final String OPT_Version = "version";
    private static final String OPT_M = "m";
    private static final String OPT_MERGE = "merge";
    private static final String OPT_D = "d";
    private static final String OPT_DIFF = "diff";
    private static final String OPT_A = "a";
    private static final String OPT_PREY_LIBRARY = "prey-library";
    private static final String OPT_B = "b";
    private static final String OPT_BAIT_LIBRARY = "bait-library";
    private static final String OPT_F = "f";
    private static final String OPT_FQ = "fastq";
    private static final String OPT_O = "o";
    private static final String OPT_OUT_DIR = "output";
    private static final String OPT_T = "t";
    private static final String OPT_THREADS = "threads";
    private static final String OPT_N = "n";
    private static final String OPT_NOT_MATCH_NUMS = "not-match-num";
    private static final String OPT_L = "l";
    private static final String OPT_LENGTH = "cut-length";

    // 处理传入选项和参数的方法
    public static void handleOptionsAndArguments(String[] args,Arguments arguments){
        // 构建选项和参数；
        buildOptions();
        // 解析传入的选项和参数；
        parseArguments(args,arguments);
    }

    // 创建方法打印usage信息
    private static void printUsageInformation() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(100," ",Constant.USAGE_INFO,OPTIONS," ");
    }

    // 构建参数
    private static void buildOptions() {
        // 无参选项构造
        OPTIONS.addOption(OPT_H, OPT_Help, false, "Print this help file and exit.\n");
        OPTIONS.addOption(OPT_V, OPT_Version, false, "Print the version of the program and exit.\n");

        // 有参选项构造
        OPTIONS.addOption(Option.builder(OPT_M).argName("file").longOpt(OPT_MERGE).hasArg(true)
                .desc("Input file is a list of result files in CSV format.\n")
                .type(String.class).build());
        OPTIONS.addOption(Option.builder(OPT_D).argName("files").longOpt(OPT_DIFF).hasArgs().numberOfArgs(2)
                .desc("Check the different PPIs of the two result files in CSV format.\n")
                .type(String.class).build());
        OPTIONS.addOption(Option.builder(OPT_A).argName("file").longOpt(OPT_PREY_LIBRARY).hasArg(true)
                .desc("Input file for prey library in CSV format.\n")
                .type(String.class).build());
        OPTIONS.addOption(Option.builder(OPT_B).argName("file").longOpt(OPT_BAIT_LIBRARY).hasArg(true)
                .desc("Input file for bait library in CSV format.\n")
                .type(String.class).build());
        OPTIONS.addOption(Option.builder(OPT_F).argName("file").longOpt(OPT_FQ).hasArg(true)
                .desc("Input file for sequencing in FASTQ format.\n")
                .type(String.class).build());
        OPTIONS.addOption(Option.builder(OPT_O).argName("file").longOpt(OPT_OUT_DIR).hasArg(true)
                .desc("Specify the output of the result file.\n")
                .type(String.class).build());
        OPTIONS.addOption(Option.builder(OPT_T).argName("num").longOpt(OPT_THREADS).hasArg(true)
                .desc("Specify the number of threads.(Default 4)\n")
                .type(Short.TYPE).build());
        OPTIONS.addOption(Option.builder(OPT_L).argName("num").longOpt(OPT_LENGTH).hasArg(true)
                .desc("Specify the value used for unique mapping.(Default 13)\n")
                .type(Short.TYPE).build());
        OPTIONS.addOption(Option.builder(OPT_N).argName("num").longOpt(OPT_NOT_MATCH_NUMS).hasArg(true)
                .desc("Specify the value used for semi-global alignment.(Default 8)\n")
                .type(Short.TYPE).build());
    }

    // 解析参数,并作出逻辑反馈
    private static void parseArguments(String[] args, Arguments arguments) {

        // 接收并解析参数
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(OPTIONS,args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*
            针对不同的参数进行逻辑反馈:
            (1) 若传入-h/--help参数，那么则输出 usage 内容,并且返回;
            (2) 若传入-v/--version参数，那么则输出 version 信息,并且返回;
            (3) 若无核心参数传入，那么则输出 usage，并且返回；

            (4) 若传入了 -m/--merge 参数，那么则只执行 merge 方法，并且返回；
            (5) 若传入了-d/--diff 参数，那么则只执行 diff 方法，并且返回；

            (6) 若传入-a/--prey-library的文件存在，那么按需分配任务；若传入的文件不存在（路径错误）则输出【error】;
            (7) 若传入-b/--bait-library的文件存在，那么按需分配任务；若传入的文件不存在（路径错误）则输出【error】;

            (8) 若传入-l/--cut-length为数字，则正确使用，将MmeI-ATTL-MmeI的两端切掉相应数量用于匹配，若传入错误则输出【error】；默认参数为13；

            (9) 若传入-f/--fastq的文件存在，那么按需分配任务；若传入的文件不存在（路径错误）则输出【error】；

            (10) 若传入-o/--outdir的信息直接使用，若路径不存在则自己创建;默认路径为【./output/[your_fastq_file_name]_PPIResult.csv】
            (11) 若传入-t/--threads为数字，则正确使用；若传入错误则输出【error】；默认参数为4；
            (12) 若传入-n/--not-match-num为数字，则正确使用，设定阈值为66-num，若传入错误则输出【error】；默认参数为8；
         */

        // (1) 若传入-h/--help参数，那么则输出 usage 内容,并且返回;
        if (cmd.hasOption(OPT_H)) {
            System.out.println();
            printUsageInformation();
            return;
        }

        // (2) 若传入-v/--version参数，那么则输出 version 信息,并且返回;
        if (cmd.hasOption(OPT_V)) {
            System.out.println();
            System.out.println(Constant.VERSION_INFO);
            return;
        }

        // (3) 若无核心参数传入，那么则输出 usage;
        if (!cmd.hasOption(OPT_A) && !cmd.hasOption(OPT_B) && !cmd.hasOption(OPT_M) && !cmd.hasOption(OPT_D)) {
            System.out.println("\nPlease look at the Usage!\n");
            printUsageInformation();
            return;
        }

        // (4) 若传入了 -m/--merge 参数，那么则只执行 merge 方法，并且返回；
        if (cmd.hasOption(OPT_M)) {
            String mergeFilePath = cmd.getOptionValue(OPT_M);
            // 判断文件是否存在
            if (Utils.isExists(mergeFilePath)) arguments.setMergeFilePath(mergeFilePath);
            else System.out.println("\n【Error】: The file for -m/--merge option isn't Existential.\n");
            // 获取outpath路劲
            if (cmd.hasOption(OPT_O)) arguments.setOutputFilePath(cmd.getOptionValue(OPT_O));
            else arguments.setOutputFilePath("./output/MergePPIResults.csv");
            return;
        }

        // (5) 若传入了-d/--diff 参数，那么则只执行 diff 方法，并且返回；
        if (cmd.hasOption(OPT_D)) {
            String[] diffFilePaths = cmd.getOptionValues(OPT_D);
            // 判断文件是否存在
            if (Utils.isExists(diffFilePaths[0]) && Utils.isExists(diffFilePaths[1])) arguments.setDiffFilePaths(diffFilePaths);
            else System.out.println("\n【Error】: The file for -d/--diff option isn't Existential.\n");
            return;
        }

        // (6) 若传入-a/--prey-library的文件存在，那么按需分配任务；若传入的文件不存在（路径错误）则输出【error】;
        if (cmd.hasOption(OPT_A)) {
            String preyLibraryPath = cmd.getOptionValue(OPT_A);
            // 判断文件是否存在
            if (Utils.isExists(preyLibraryPath)) arguments.setPreyLibraryPath(preyLibraryPath);
            else System.out.println("\n【Error】: The file for -a/--prey-library option isn't Existential.\n");
        }

        //  (7) 若传入-b/--bait-library的文件存在，那么按需分配任务；若传入的文件不存在（路径错误）则输出【error】;
        if (cmd.hasOption(OPT_B)) {
            String baitLibraryPath = cmd.getOptionValue(OPT_B);
            // 判断文件是否存在
            if (Utils.isExists(baitLibraryPath)) arguments.setBaitLibraryPath(baitLibraryPath);
            else System.out.println("\n【Error】: The file for -b/--bait-library option isn't Existential.\n");
        }

        // (8) 若传入-l/--cut-length为数字，则正确使用，将MmeI-ATTL-MmeI的两端切掉相应数量用于匹配，若传入错误则输出【error】；默认参数为13；
        if (cmd.hasOption(OPT_L)) {
            String cutLength = cmd.getOptionValue(OPT_L);
            // 判断传入的参数是否为数字
            if (Utils.isInteger(cutLength)) arguments.setCutLength(Integer.valueOf(cutLength));
            else System.out.println("\n【Error】: The argument for -l/--cut-length option need a number.\n");
        }

        // (9) 若传入-f/--fastq的文件存在，那么按需分配任务；若传入的文件不存在（路径错误）则输出【error】；
        if (cmd.hasOption(OPT_F)) {
            String fastqFilePath = cmd.getOptionValue(OPT_F);
            // 判断文件是否存在
            if (Utils.isExists(fastqFilePath)) arguments.setFastqFilePath(fastqFilePath);
            else System.out.println("\n【Error】: The file for -f/--fastq option isn't Existential.\n");
        }

        // (10) 若传入-o/--outdir的信息直接使用，若路径不存在则自己创建;默认路径为【./output/[your_fastq_file_name]_PPIResult.csv】
        if (cmd.hasOption(OPT_O)) arguments.setOutputFilePath(cmd.getOptionValue(OPT_O));
        else {
            // 如果有 OPT_F 参数
            if (cmd.hasOption(OPT_F)) {
                // 获取文件名
                String fileName = FileUtil.getName(arguments.getFastqFilePath());
                // 获取文件名前缀
                String prefixName = FileUtil.getName(fileName);
                arguments.setOutputFilePath("./output/"+ prefixName+"_PPIResult.csv");
            }
        };

        // (11) 若传入-t/--threads为数字，则正确使用；若传入错误则输出【error】；默认参数为4；
        if (cmd.hasOption(OPT_T)) {
            String threadsNum = cmd.getOptionValue(OPT_T);
            // 判断传入的参数是否为数字
            if (Utils.isInteger(threadsNum)) arguments.setThreadNum(Integer.valueOf(threadsNum));
            else System.out.println("\n【Error】: The argument for -t/--thread option need a number.\n");
        }

        // (12) 若传入-n/--not-match-num为数字，则正确使用，设定阈值为66-num，若传入错误则输出【error】；默认参数为8；
        if (cmd.hasOption(OPT_N)) {
            String notMatchNums = cmd.getOptionValue(OPT_NOT_MATCH_NUMS);
            // 判断传入的参数是否为数字
            if (Utils.isInteger(notMatchNums)) arguments.setNotMatchNums(Integer.valueOf(notMatchNums));
            else System.out.println("\n【Error】: The argument for -n/--not-match-num option need a number.\n");
        }
    }
}
