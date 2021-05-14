package com.hzau.options.domain;

import lombok.Data;

@Data
public class Arguments {

    // 指定输入prey参考文库路径
    private String preyLibraryPath;

    // 指定输入bait参考文库路径
    private String baitLibraryPath;

    // 指定输入的fastq格式的测序文件路径
    private String fastqFilePath;

    // 指定【-m】参数的输入对象，该文件需要是路径信息的集合
    private String mergeFilePath;

    // 指定需要比对的csv文件路径
    private String[] diffFilePaths;

    // 指定的线程数，默认线程数为4
    private int threadNum;

    // 指定输出文件的路径信息
    // 【-m】参数的默认为【./output/MergePPIResults.csv】
    // 【-f】参数的默认为【./output/[your_fastq_file_name]_PPIResult.csv】
    private String outputFilePath;

    // 指定align to MmeI-ATTTL-MmeI序列时,允许错配的数量,默认为8
    private int notMatchNums;

    // 最终用于匹配至AD和BD文库的剪切序列长度,默认为13 bp
    private int cutLength;

    // 无参构造
    public Arguments() {
        // 线程数默认为4个线程
        this.threadNum = 4;
        // 允许错配的数量默认值为8
        this.notMatchNums = 8;
        // 剪切序列长度默认值为13
        this.cutLength = 13;
    }
}
