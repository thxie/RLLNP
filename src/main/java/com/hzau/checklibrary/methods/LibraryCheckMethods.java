package com.hzau.checklibrary.methods;

import com.hzau.Constant;
import com.hzau.Entrance;
import com.hzau.Utils;
import com.hzau.options.domain.Arguments;
import com.hzau.checklibrary.domain.LibraryInfo;
import java.util.*;

/**
 * 任务：
 * (1) findDuplicateEle方法：bait文库和prey文库中是否有基因重名现象;
 * (2) printErrorMessage方法：将基因序列从原始reads的末尾修剪到13bp(反向引物的前13bp),
 *     并且在AD文库和BD文库中找是否有相同标记序列；
 */

public class LibraryCheckMethods {

    // 利用多线程，处理Prey和Bait文库的信息检查问题
    // 分别在bait文库和prey文库中查找是否有重复的 gene Name 和 markSeq
    public static void checkDoubleLibrary(LibraryInfo libraryInfo, Arguments arguments) {
        System.out.println(Constant.LIBRARY_CHECK_INFO);
        Thread preyCheckLibrary = new Thread(new Runnable() {
            @Override
            public void run() {
                if (arguments.getPreyLibraryPath() != null) {
                    // 在Prey文库中查找重复的基因名称
                    Set<String> nameListRes = Utils.findDuplicateEle(libraryInfo.getPreyLibraryName());
                    // 在Prey文库中查找重复的基因序列(末端13bp)
                    Set<String> seqListRes = Utils.findDuplicateEle(libraryInfo.getPreyLibrarySeq());
                    // 打印prey文库的错误信息
                    printErrorMessage(libraryInfo,nameListRes,seqListRes,"prey");
                    // 放空两个用于检查的集合内存
                    libraryInfo.setPreyLibraryName(null);
                    libraryInfo.setPreyLibrarySeq(null);
                }
            }
        }, "preyCheckLibrary");

        Thread baitCheckLibrary = new Thread(new Runnable() {
            @Override
            public void run() {
                if (arguments.getBaitLibraryPath() != null){
                    Set<String> nameListRes = Utils.findDuplicateEle(libraryInfo.getBaitLibraryName());
                    Set<String> seqListRes = Utils.findDuplicateEle(libraryInfo.getBaitLibrarySeq());
                    printErrorMessage(libraryInfo,nameListRes,seqListRes,"bait");
                    libraryInfo.setBaitLibraryName(null);
                    libraryInfo.setBaitLibrarySeq(null);
                }
            }
        }, "baitCheckLibrary");
        preyCheckLibrary.start();
        baitCheckLibrary.start();
        try {
            // 等待运行完毕后再执行后面的进程
            preyCheckLibrary.join();
            baitCheckLibrary.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 输出错误信息，并且加了静态锁，输出都必须排队；
    private synchronized static void printErrorMessage(LibraryInfo libraryInfo, Set<String> nameListRes,
                                                       Set<String> seqListRes, String mark){
        if ("prey".equals(mark)) {
            if (libraryInfo.getPreyLibrarySeq().size() == 0) {
                System.out.println("\n【Error】: Your prey library is empty! Please check it!\n");
                return;
            }
            if (nameListRes == null && seqListRes == null) {
                Entrance.signal = true;
                System.out.println("【Prey Library format is right!】\n");
            } else if (seqListRes == null && nameListRes != null) {
                System.out.println("【Error】: There some duplicate names in your prey library:");
                System.out.println(nameListRes+"\n");
            } else if (seqListRes != null && nameListRes == null) {
                System.out.println("【Error】: There some duplicate mark sequences in your prey library:");
                System.out.println(seqListRes+"\n");
            } else {
                System.out.println("【Error】: There some duplicate names in your prey library:");
                System.out.println(nameListRes+"\n");
                System.out.println("【Error】: There some duplicate mark sequences in your prey library:");
                System.out.println(seqListRes+"\n");
            }
        }

        if ("bait".equals(mark)) {
            if (libraryInfo.getBaitLibrarySeq().size() == 0) {
                System.out.println("\n【Error】: Your bait library is empty! Please check it!】\n");
                return;
            }
            if (nameListRes == null && seqListRes == null) {
                Entrance.signal = true;
                System.out.println("【Bait Library format is right!】\n");
            } else if (seqListRes == null && nameListRes != null) {
                System.out.println("【Error】: There some duplicate names in your bait library:");
                System.out.println(nameListRes+"\n");
                System.out.println();
            } else if (seqListRes != null && nameListRes == null) {
                System.out.println("【Error】: There some duplicate mark sequences in your bait library:");
                System.out.println(seqListRes+"\n");
            } else {
                System.out.println("【Error】: There some duplicate names in your bait library:");
                System.out.println(nameListRes+"\n");
                System.out.println("【Error】: There some duplicate mark sequences in your bait library:");
                System.out.println(seqListRes+"\n");
            }
        }
    }
}
