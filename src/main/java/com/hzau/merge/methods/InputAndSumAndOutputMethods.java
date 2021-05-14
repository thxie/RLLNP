package com.hzau.merge.methods;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.hzau.Constant;
import com.hzau.options.domain.Arguments;
import com.hzau.merge.domain.Merge_Info;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InputAndSumAndOutputMethods {

    // 将几个CSV文件合并，并且输出最终结果CSV文件；
    public static void sumAndOutputMergePPIFiles(Arguments arguments,Merge_Info merge_info){
        System.out.println(Constant.WELCOME_INFO);
        System.out.println(Constant.MERGE_INFO);
        List<String> fileList = inputCSVFilesList(arguments);
        inputCSVFilesAndSum(fileList,merge_info);
        outputMergeCSVFile(arguments,merge_info);
        System.out.println(StrUtil.format(Constant.MERGE_OUTPUT_PATH,arguments.getOutputFilePath()));
        System.out.println(Constant.FINAL);
    }

    private static List<String> inputCSVFilesList(Arguments arguments) {
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader in = null;
        // 创建临时变量
        String str;
        List<String> files = new ArrayList<>();
        try {
            fis = new FileInputStream(arguments.getMergeFilePath());
            isr = new InputStreamReader(fis, "UTF-8");
            in = new BufferedReader(isr);
            // 读取数据
            while ((str = in.readLine()) != null) files.add(str);
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
            return files;
        }
    }

    private static void inputCSVFilesAndSum(List<String> list,Merge_Info merge_info) {
        for (String filepath : list) {
            FileInputStream fis;
            InputStreamReader isr;
            BufferedReader in = null;
            // 创建临时变量
            String str;
            try {
                fis = new FileInputStream(filepath);
                isr = new InputStreamReader(fis, "UTF-8");
                in = new BufferedReader(isr);
                // 读取数据
                while ((str = in.readLine()) != null) sumToMergePPIResult(str,merge_info);
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

    private static void sumToMergePPIResult(String PPI, Merge_Info merge_info){
        String[] temp = PPI.split(",");
        // 第一行表头信息不统计;
        if ("Prey_Library_Name".equals(temp[0])) return;
        String PPINamePart = temp[0]+","+temp[1];
        int PPIFrequency = Integer.valueOf(temp[2]);
        if (merge_info.getMergePPIResults().containsKey(PPINamePart)) {
            int sum = merge_info.getMergePPIResults().get(PPINamePart)+PPIFrequency;
            merge_info.getMergePPIResults().put(PPINamePart,sum);
        } else merge_info.getMergePPIResults().put(PPINamePart,PPIFrequency);
    }


    // 将结果输出至 outputPath 的 csv 文件中；
    private static void outputMergeCSVFile(Arguments arguments,Merge_Info merge_info) {
        Map<String,Integer> ppi_merge_res = merge_info.getMergePPIResults();
        Set<String> ppi_merge_res_name_set = ppi_merge_res.keySet();
        CsvWriter writer = CsvUtil.getWriter(arguments.getOutputFilePath(), CharsetUtil.CHARSET_UTF_8);
        // 先输出表头;
        writer.write(new String[] {"Prey_Library_Name","Bait_Library_Name","Frequency"});
        // 再输出内容;
        for (String ppi_merge_res_name : ppi_merge_res_name_set) {
            String[] names = ppi_merge_res_name.split(",");
            String[] output = new String[3];
            output[0] = names[0];
            output[1] = names[1];
            output[2] = String.valueOf(ppi_merge_res.get(ppi_merge_res_name));
            writer.write(output);
        }
    }
}
