package com.hzau.diff.methods;

import cn.hutool.core.util.StrUtil;
import com.hzau.Constant;
import com.hzau.options.domain.Arguments;
import com.hzau.diff.domain.Diff_Info;
import java.io.*;
import java.util.*;

public class InputAndDiffAndOutputMethods {

    public static void diffAndOutDiffInfo(Arguments arguments, Diff_Info diff_info) {
        System.out.println(Constant.WELCOME_INFO);
        System.out.println(Constant.DIFF_INFO);
        String[] diffFilePath = arguments.getDiffFilePaths();
        inputCSVFilesAndDiff(diffFilePath[0],diff_info,0);
        inputCSVFilesAndDiff(diffFilePath[1],diff_info,1);
        List<String> diffRes = checkDiff(diff_info);
        if (diffRes.size() == 0) System.out.println(Constant.DIFF_RIGHT);
        else System.out.println(StrUtil.format(Constant.DIFF_ERROR,diffRes));
        System.out.println(Constant.FINAL);
    }

    private static void inputCSVFilesAndDiff(String path, Diff_Info diff_info,int mark) {
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader in = null;
        // 创建临时变量
        String str;
        try {
            fis = new FileInputStream(path);
            isr = new InputStreamReader(fis, "UTF-8");
            in = new BufferedReader(isr);
            // 读取数据
            if (mark == 0) {
                Map<String,Integer> map1 = diff_info.getFile1_PPIResults();
                while ((str = in.readLine()) != null) getInfoToMap(str,map1);
            }
            if (mark == 1) {
                Map<String,Integer> map2 = diff_info.getFile2_PPIResults();
                while ((str = in.readLine()) != null) getInfoToMap(str,map2);
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

    private static void getInfoToMap(String PPI, Map<String,Integer> map){
        String[] temp = PPI.split(",");
        if ("Prey_Library_Name".equals(temp[0])) return;
        String PPINamePart = temp[0]+","+temp[1];
        int PPIFrequency = Integer.valueOf(temp[2]);
        map.put(PPINamePart,PPIFrequency);
    }

    private static List<String> checkDiff(Diff_Info diff_info){
        List<String> diffRes = new LinkedList<>();
        Map<String,Integer> file1 = diff_info.getFile1_PPIResults();
        Map<String,Integer> file2 = diff_info.getFile2_PPIResults();
        Set<String> filePPINameParts = file1.keySet();
        for (String namePart : filePPINameParts) {
            if (!file1.get(namePart).equals(file2.get(namePart))) diffRes.add(namePart);
        }
        return diffRes;
    }
}

