package com.hzau.output.methods;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import com.hzau.options.domain.Arguments;
import com.hzau.output.domain.PPIResultInfo;
import java.util.Map;
import java.util.Set;

public class SumAndOutputPPIResultMethods {

    // 给方法上了锁，所有线程使用该方法，都需要排队；
    // 将子集中的PPI结果汇总给TotalPPI的结果；
    public synchronized static void sumSubToTotalPPIResult(Map<String,Integer> subPPIResult, PPIResultInfo ppiResultInfo){
        Set<String> subPPIResultKeys = subPPIResult.keySet();
        for (String ppi_name : subPPIResultKeys) {
            if (ppiResultInfo.getTotalPPIResult().containsKey(ppi_name)) {
                int sum = ppiResultInfo.getTotalPPIResult().get(ppi_name)+subPPIResult.get(ppi_name);
                ppiResultInfo.getTotalPPIResult().put(ppi_name,sum);
            } else ppiResultInfo.getTotalPPIResult().put(ppi_name,subPPIResult.get(ppi_name));
        }
    }

    // 将结果输出至 outputPath 的 csv 文件中；
    public static void outputCSVFile(PPIResultInfo ppiResultInfo, Arguments arguments) {
        Map<String,Integer> ppi_res = ppiResultInfo.getTotalPPIResult();
        Set<String> ppi_res_name_set = ppi_res.keySet();
        CsvWriter writer = CsvUtil.getWriter(arguments.getOutputFilePath(), CharsetUtil.CHARSET_UTF_8);
        writer.write(new String[] {"Prey_Library_Name","Bait_Library_Name","Frequency"});
        for (String ppi_res_name : ppi_res_name_set) {
            String[] names = ppi_res_name.split(",");
            String[] output = new String[3];
            output[0] = names[0];
            output[1] = names[1];
            output[2] = String.valueOf(ppi_res.get(ppi_res_name));
            writer.write(output);
        }
    }
}
