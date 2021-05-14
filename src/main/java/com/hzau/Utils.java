package com.hzau;

import cn.hutool.core.collection.CollUtil;
import com.hzau.checklibrary.domain.LibraryInfo;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Utils {

    // (1) 判断字符串是否为数字
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    // (2) 判断文件或目录是否存在
    public static boolean isExists(String path){
        File file = new File(path);
        if (file.exists()) return true;
        return false;
    }

    // (3) 发现list的集合中的重复元素
    public static Set<String> findDuplicateEle(List<String> list) {
        Set<String> repeatedEle = new HashSet<>();
        int size = list.size();
        Map<String, Integer> hashMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            if (hashMap.get(list.get(i)) != null) {
                Integer value = hashMap.get(list.get(i));
                hashMap.put(list.get(i), value + 1);
                repeatedEle.add(list.get(i));
            } else {
                hashMap.put(list.get(i), 1);
            }
        }
        if (repeatedEle.size() == 0) return null;
        return repeatedEle;
    }

    // (4) 将5'-3‘端（正向）的序列反转成3'-5'端（反向）的序列
    public static String reverseSeq(String seq){
        // 1、对序列进行碱基互补配对
        String strList = seq.toLowerCase();
        strList = strList.replaceAll("a","T").replace("t","A").
                replaceAll("c","G").replaceAll("g","C");
        strList = strList.toUpperCase();
        // 2、并且对互补配对好的序列进行反转，返回3'-5'端的反向序列
        return new StringBuffer(strList).reverse().toString();
    }

    // (5) 判断字符串是否是DNA序列
    public static boolean isDNASeq(String seq){
        return seq.contains("C") && seq.contains("G") && seq.contains("T") && seq.contains("A");
    }

    // (6) 获取文件行数
    public static int getFileLineNum(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))){
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber;
        } catch (IOException e) {
            return -1;
        }
    }

    // (7) 获取gz文件行数
    public static int getGzFileLineNum(String filePath) {
        // 字节流：先从gz压缩文件中读取数据
        FileInputStream fis = null;
        // gz解压缩流：将
        GZIPInputStream gzis = null;
        // 转换流:将字节流转换为字符流；
        InputStreamReader reader = null;
        // 缓冲流:将字符流包装成缓冲流，目的是为了减少物理读写次数，增加效率
        BufferedReader in = null;
        // 获取文件行数
        int fileLineNum = 0;
        // 记录每一行的文本信息
        String str;
        try {
            fis = new FileInputStream(filePath);
            gzis = new GZIPInputStream(fis);
            reader = new InputStreamReader(gzis);
            in = new BufferedReader(reader);
            // 读取数据
            while ((str = in.readLine()) != null) fileLineNum++;
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
            return fileLineNum;
        }
    }

    // (8) 判断文库中是否含有 subList 中列表的两边序列；
    // 如果是subList_53(正向序列),左侧是bait文库(数组index=0)，右侧是prey文库(数组index=1)；
    public static boolean containsThisSeq_53 (String[] subSeqArray, LibraryInfo libraryInfo) {
        // 先判断左侧的 cutLength 长度的序列是否出现在 BaitLibrary（5’-3‘，正向） 中；
        boolean isBait = libraryInfo.getBaitLibrarySeqAndName().containsKey(subSeqArray[0]);
        // 再判断右侧的 cutLength 长度的序列是否出现在 PreyLibrary(3'-5'，反向)中；
        boolean isPrey = libraryInfo.getPreyLibraryReverseSeqAndName().containsKey(subSeqArray[1]);
        // 返回两边都存在的boolean
        return isBait && isPrey;
    }

    // (9) 如果是subList_35(正向序列),左侧是prey文库(数组index=0)，右侧是bait文库(数组index=1)；
    public static boolean containsThisSeq_35 (String[] subSeqArray, LibraryInfo libraryInfo) {
        // 先判断左侧的 cutLength 长度的序列是否出现在 PreyLibrary（5’-3‘,正向） 中；
        boolean isPrey = libraryInfo.getPreyLibrarySeqAndName().containsKey(subSeqArray[0]);
        // 先判断右侧的 cutLength 长度的序列是否出现在 BaitLibrary(3'-5',反向) 中；
        boolean isBait = libraryInfo.getBaitLibraryReverseSeqAndName().containsKey(subSeqArray[1]);
        // 返回两边都存在的boolean
        return isBait && isPrey;
    }

    // (10) map集合，统计元素个数
    public static void mapEleCounts(Map<String,Integer> res,String ele) {
        if (res.containsKey(ele)) res.put(ele,res.get(ele)+1);
        else res.put(ele,1);
    }

    // (11) 将list拆分成多给指定的大小的list
    public static List<Object[]>[] subList(List<Object[]> targetList, int threadsNum) {
        List<Object[]>[] subFqSeqArray = new List[threadsNum];
        int size = targetList.size()/threadsNum;
        for (int i = 0,n = 1; i < threadsNum; i++,n++) {
            if (n != threadsNum) subFqSeqArray[i] = CollUtil.sub(targetList,(n-1)*size,n*size);
            else subFqSeqArray[i] = CollUtil.sub(targetList,(n-1)*size,targetList.size());
        }
        return subFqSeqArray;
    }

    // (12) 将毫秒数修改为 []min []s []ms 输出格式
    public static String ConvertToMin(Long totalTime) {
        Long min = totalTime / 60000;
        Long s = totalTime % 60000 / 1000;
        Long ms = totalTime % 60000 % 1000;
        return min + "min " + s + "s " + ms + "ms";
    }

    // (13) 获取当前 处理好的行数/文件的行数
    public static double getDecimal(int fileLineNum,int counts) {
        return (double)counts/(double)fileLineNum;
    }

}
