package com.hzau.checklibrary.methods;

import com.hzau.Utils;
import com.hzau.checklibrary.domain.LibraryInfo;
import com.hzau.options.domain.Arguments;
import java.io.*;

/**
 * 任务：
 * (1) 定义输入csv格式,高效IO流,输入两个文库的信息;
 */

public class LibraryInputMethod {

    // 处理两个文库的输入问题
    public static void inputDoubleLibrary(Arguments arguments, LibraryInfo libraryInfo) {
        // 直接使用主线程进行 AD 和 BD 参考文库文件读取
        if (arguments.getPreyLibraryPath() != null) libraryInputStream(arguments, libraryInfo, "prey");
        if (arguments.getBaitLibraryPath() != null) libraryInputStream(arguments, libraryInfo, "bait");
    }

    // 利用缓冲流来读取prey和bait文库的csv文件
    private static void libraryInputStream(Arguments arguments, LibraryInfo libraryInfo, String mark) {
        // 打开文件流和缓冲流(readline)
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        // 创建临时变量
        String str;
        int seqLength;
        int cutLength = arguments.getCutLength();
        String seq;
        String geneName;
        try {
            // 判断输入的是prey文库或bait文库；
            if ("prey".equals(mark)) fis = new FileInputStream(arguments.getPreyLibraryPath());
            if ("bait".equals(mark)) fis = new FileInputStream(arguments.getBaitLibraryPath());
            isr = new InputStreamReader(fis, "UTF-8");
            bufferedReader = new BufferedReader(isr);
            // 按不同文库路径，按行读取csv文件；
            if ("prey".equals(mark)) {
                while ((str = bufferedReader.readLine()) != null) {
                    // 将csv文件中的基因名和基因序列信息分来至数组中；
                    String[] info = str.split(",");
                    // 获取基因名称
                    geneName = info[0].trim();
                    // 获取基因序列的长度
                    seqLength = info[1].trim().length();
                    // 获取基因序列的靠近末端的 cutLength bp 的大写序列
                    seq = info[1].trim().substring(seqLength - cutLength, seqLength).toUpperCase();
                    // 将prey文库的基因名称传入至preyLibraryName中；
                    libraryInfo.getPreyLibraryName().add(geneName);
                    // 将prey文库的末端cutLength bp的正向序列传入至preyLibrarySeq中；
                    libraryInfo.getPreyLibrarySeq().add(seq);
                    // 将prey文库的末端cutLength bp的正向序列和prey文库基因名称加入至preyLibraryName；
                    libraryInfo.getPreyLibrarySeqAndName().put(seq,geneName);
                    // 将preyLibrarySeq序列的信息从5'-3'端反转至3'-5'端后和prey文库基因名称输入至preyLibraryReverseSeq
                    libraryInfo.getPreyLibraryReverseSeqAndName().put(Utils.reverseSeq(seq),geneName);
                }
            }
            if ("bait".equals(mark)) {
                while ((str = bufferedReader.readLine()) != null) {
                    String[] info = str.split(",");
                    geneName = info[0].trim();
                    seqLength = info[1].trim().length();
                    seq = info[1].trim().substring(seqLength - cutLength, seqLength).toUpperCase();
                    libraryInfo.getBaitLibraryName().add(geneName);
                    libraryInfo.getBaitLibrarySeq().add(seq);
                    libraryInfo.getBaitLibrarySeqAndName().put(seq,geneName);
                    libraryInfo.getBaitLibraryReverseSeqAndName().put(Utils.reverseSeq(seq),geneName);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    // 关闭缓冲流
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//                System.out.println(StrUtil.format(Constant.PREY_LIBRARY_OUTPUT_53,libraryInfo.getPreyLibrarySeqAndName().size(),
//                        libraryInfo.getPreyLibrarySeqAndName()));
//                System.out.println(StrUtil.format(Constant.PREY_LIBRARY_OUTPUT_35,libraryInfo.getPreyLibraryReverseSeqAndName().size(),
//                        libraryInfo.getPreyLibraryReverseSeqAndName()));


//                System.out.println(StrUtil.format(Constant.BAIT_LIBRARY_OUTPUT_53,libraryInfo.getBaitLibrarySeqAndName().size(),
//                        libraryInfo.getBaitLibrarySeqAndName()));
//                System.out.println(StrUtil.format(Constant.BAIT_LIBRARY_OUTPUT_35,libraryInfo.getBaitLibraryReverseSeqAndName().size(),
//                        libraryInfo.getBaitLibraryReverseSeqAndName()));