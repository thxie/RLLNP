package com.hzau;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import com.hzau.checklibrary.domain.LibraryInfo;
import com.hzau.checklibrary.methods.LibraryCheckMethods;
import com.hzau.checklibrary.methods.LibraryInputMethod;
import com.hzau.mapping.domain.MappingInfo;
import com.hzau.options.domain.Arguments;
import com.hzau.options.methods.ArgumentsInput;
import com.hzau.mapping.methods.FileInputAndCoreAlgorithm;
import com.hzau.output.domain.PPIResultInfo;
import com.hzau.output.methods.SumAndOutputPPIResultMethods;
import com.hzau.merge.domain.Merge_Info;
import com.hzau.merge.methods.InputAndSumAndOutputMethods;
import com.hzau.diff.domain.Diff_Info;
import com.hzau.diff.methods.InputAndDiffAndOutputMethods;


public class Entrance {

    // 用于判断的逻辑符号，若文库文件错误，那么就不需要继续向下处理了;
    public static Boolean signal = false;

    // 主方法，参数信息会从形参中传入
    public static void main(String[] args) {

        // 计算总计算时间
        TimeInterval timer = DateUtil.timer();

        // 创建输入参数的实体类对象
        Arguments arguments = new Arguments();

        // (1) 调用处理传入选项和参数
        ArgumentsInput.handleOptionsAndArguments(args, arguments);

        // (2.1) 如果merge参数不为空，那么就直接执行 merge 命令，并且返回方法
        if (arguments.getMergeFilePath() != null) {
            Merge_Info merge_info = new Merge_Info();
            InputAndSumAndOutputMethods.sumAndOutputMergePPIFiles(arguments,merge_info);
            return;
        }

        // (2.2) 如果diff参数不为空，那么就直接执行 diff 命令，并且返回方法
        if (arguments.getDiffFilePaths() != null) {
            Diff_Info diff_info = new Diff_Info();
            InputAndDiffAndOutputMethods.diffAndOutDiffInfo(arguments,diff_info);
            return;
        }

        // 如果两个文库文件传入参数都为空，那么就不进行文库检查处理，主要针对-h，-v参数:
        if (arguments.getPreyLibraryPath() == null && arguments.getBaitLibraryPath() == null) return;

        // (3) 输出欢迎信息
        System.out.println(Constant.WELCOME_INFO);

        // 创建文库文件的实体类对象
        LibraryInfo libraryInfo = new LibraryInfo();

        // (4) 处理Prey/Bait文库的输入问题
        LibraryInputMethod.inputDoubleLibrary(arguments, libraryInfo);

        // (5) 利用双线程，处理Prey/Bait文库的信息检查问题(重复基因名问题和末端 cutLength bp 重复序列的问题)
        LibraryCheckMethods.checkDoubleLibrary(libraryInfo,arguments);

        // 若信号被标记为false，则说明文库文件有问题，那么则不运行后面的半全局比对算法
        // 如果fastq文件路径为空，那么则不运行后面的半全局比对算法
        if (signal == false || arguments.getFastqFilePath() == null) {
            System.out.println(Constant.FINAL);
            return;
        }
        // 若AD和BD其中有一个文库为空，那么也不运行后面的比对算法
        if (arguments.getPreyLibraryPath() == null || arguments.getBaitLibraryPath() == null) {
            System.out.println(Constant.LIBRARY_ERROR_INFO);
            System.out.println(Constant.FINAL);
            return;
        };

        // 输出参数信息
        System.out.println(StrUtil.format(Constant.ARGUMENTS_INFO,arguments.getPreyLibraryPath(),arguments.getBaitLibraryPath(),
                arguments.getFastqFilePath(),arguments.getOutputFilePath(),arguments.getThreadNum(),arguments.getNotMatchNums(),
                arguments.getCutLength()));

        // 创建mappingInformation实体类对象
        MappingInfo mappingInfo = new MappingInfo();

        // 创建蛋白质互作网络的结果集实体类对象
        PPIResultInfo ppiResultInfo = new PPIResultInfo();

        // (5) 处理 fq 文件的输入问题 + 半全局比对算法 + 末端 cutLength bp序列唯一比对至文库操作
        System.out.println(Constant.ALIGNMENT_INFO);
        FileInputAndCoreAlgorithm.handleInputAndGetResults(arguments, mappingInfo,libraryInfo,ppiResultInfo);

        // (6) 将最终PPI结果输出至 outputPath 的 csv 文件中；
        SumAndOutputPPIResultMethods.outputCSVFile(ppiResultInfo,arguments);

        // 计算总时间
        mappingInfo.setTotalTime(timer.interval());

        // (7) 输出统计信息
        System.out.println(StrUtil.format(Constant.STATISTICS_INFO, Utils.ConvertToMin(mappingInfo.getTotalTime()),
                mappingInfo.getFqFileReadsCounts(), mappingInfo.getAlignToMarkSeqCounts(),ppiResultInfo.getMapPPIResultCounts()));
        System.out.println(Constant.FINAL);
    }
}

