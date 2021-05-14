package com.hzau.mapping.methods;

import cn.hutool.core.util.StrUtil;
import com.hzau.Constant;
import com.hzau.Utils;
import com.hzau.checklibrary.domain.LibraryInfo;
import com.hzau.mapping.domain.MappingInfo;
import com.hzau.options.domain.Arguments;
import com.hzau.output.domain.PPIResultInfo;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class FileInputAndCoreAlgorithm {

    // 处理单端测序文件的输入和核心计算的问题
    public static void handleInputAndGetResults(Arguments arguments, MappingInfo mappingInfo,
                                        LibraryInfo libraryInfo, PPIResultInfo ppiResultInfo) {

        if (arguments.getFastqFilePath().contains(".gz"))handleFastqGzFile(arguments, mappingInfo,libraryInfo,ppiResultInfo);
        else handleFastqFile(arguments,mappingInfo,libraryInfo,ppiResultInfo);
    }

    /*
        (1) 利用缓冲流来读取fastq.gz的测序文件中的DNA序列，序列需要大于【2*CutLength+66】;
        (2) reads每到25000行的倍数，或者文件到最末尾，处理一次半全局比对算法，以及后面的结果统计流程；
        (3) 根据处理好的reads数，来计算和输出流程进度的百分比%
     */

    private static void handleFastqGzFile(Arguments arguments, MappingInfo mappingInfo,
                                          LibraryInfo libraryInfo, PPIResultInfo ppiResultInfo) {
        // 字节流：先从gz压缩文件中读取数据
        FileInputStream fis;
        // gz解压缩流：将
        GZIPInputStream gzis;
        // 转换流:将字节流转换为字符流；
        InputStreamReader reader;
        // 缓冲流:将字符流包装成缓冲流，目的是为了减少物理读写次数，增加效率
        BufferedReader in = null;
        // 获取文件路劲
        String path = arguments.getFastqFilePath();
        // 获取文件的行数
        int fileReadsLineNum = Utils.getGzFileLineNum(path)/4;
        // 将文件中的reads信息总数输出
        mappingInfo.setFqFileReadsCounts(fileReadsLineNum);
        // 记录每一行的文本信息
        String str;
        // 记录reads信息
        Object[] info;
        // 记录读取文件的行数和一个过程输出temp变量
        int counts = 0;
        double n = 0;
        try {
            fis = new FileInputStream(path);
            gzis = new GZIPInputStream(fis);
            reader = new InputStreamReader(gzis, "UTF-8");
            in = new BufferedReader(reader);
            // 读取数据
            while ((str = in.readLine()) != null) {
                // 获取fastq测序文件中的DNA序列
                if (Utils.isDNASeq(str)) {
                    info = new Object[2];
                    info[0] = false;
                    info[1] = str.trim();
                    mappingInfo.getFastq_seq().add(info);
                    counts++;
                }
                // reads每到25000行的倍数，或者文件到最末尾，处理一次计算模式；
                if ((counts >= 25000 && counts % 25000 == 0) || counts == fileReadsLineNum){
                    // (6.1) 利用多线程，处理半全局比对问题，并且将markSeq两端至 cutLength bp保存起来，然后与文库信息比对，获取最后PPI结果。
                    MulThreadsForGetPPIResults.mulThreads(mappingInfo,arguments,libraryInfo,ppiResultInfo);
                    // 输出过程信息
                    if (Utils.getDecimal(fileReadsLineNum,counts) >= 0.05*n) {
                        System.out.println(StrUtil.format(Constant.ALIGNMENT_PROCESS_INFO,5*n++));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    // 关闭流
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
        (1) 利用缓冲流来读取fastq的测序文件中的DNA序列;
        (2) reads每到25000行的倍数，或者文件到最末尾，处理一次半全局比对算法，以及后面的结果统计流程；
        (3) 根据处理好的reads数，来计算和输出流程进度的百分比%
     */

    private static void handleFastqFile(Arguments arguments, MappingInfo mappingInfo,
                                        LibraryInfo libraryInfo, PPIResultInfo ppiResultInfo) {
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader in = null;
        // 获取文件路劲
        String path = arguments.getFastqFilePath();
        // 获取文件的行数
        int fileReadsLineNum = Utils.getFileLineNum(path)/4;
        // 将文件中的reads信息总数输出
        mappingInfo.setFqFileReadsCounts(fileReadsLineNum);
        // 记录每一行的文本信息
        String str;
        // 记录reads信息
        Object[] info;
        // 记录文件行的数量和一个过程输出temp变量
        int counts = 0;
        double n = 0;
        try {
            fis = new FileInputStream(arguments.getFastqFilePath());
            isr = new InputStreamReader(fis, "UTF-8");
            in = new BufferedReader(isr);
            // 读取数据
            while ((str = in.readLine()) != null) {
                // 获取 fastq 测序文件中的DNA序列
                if (Utils.isDNASeq(str)) {
                    info = new Object[2];
                    info[0] = false;
                    info[1] = str.trim();
                    mappingInfo.getFastq_seq().add(info);
                    counts++;
                }
                // reads每到25000行的倍数，或者文件到最末尾，处理一次计算模式；
                if ((counts >= 25000 && counts % 25000 == 0)  || counts == fileReadsLineNum){
                    // (6.1) 利用多线程，处理半全局比对问题，并且将markSeq两端至 cutLength bp保存起来，然后与文库信息比对，获取最后PPI结果。
                    MulThreadsForGetPPIResults.mulThreads(mappingInfo,arguments,libraryInfo,ppiResultInfo);
                    // 输出过程信息
                    if (Utils.getDecimal(fileReadsLineNum,counts) >= 0.05*n) {
                        System.out.println(StrUtil.format(Constant.ALIGNMENT_PROCESS_INFO,5*n++));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    // 关闭流
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


//                    System.out.println((counts)+"|25000 reads input time：" + Utils.ConvertToMin2(timer.intervalRestart()));
//                    System.out.println((counts)+"|25000 reads has been aligned, use time:"+ Utils.ConvertToMin2(timer.intervalRestart()));

// (6.2) 利用单线处理，主要为了验证
//                    AlignmentAndGetPPIResultThead.handleAlignmentAndGetPPIResult(mappingInfo.getFastq_seq(),mappingInfo,arguments,libraryInfo,ppiResultInfo);