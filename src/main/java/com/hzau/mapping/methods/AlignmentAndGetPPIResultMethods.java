package com.hzau.mapping.methods;

import cn.hutool.core.util.StrUtil;
import com.hzau.Utils;
import com.hzau.checklibrary.domain.LibraryInfo;
import com.hzau.mapping.domain.MappingInfo;
import com.hzau.options.domain.Arguments;
import com.hzau.output.domain.PPIResultInfo;
import java.util.*;

public class AlignmentAndGetPPIResultMethods {

    /*
        设置打分值:
            (1) 两个碱基完全匹配上的加分值[+1]
            (2) 两个碱基错配的加分值[0]
            (3) 其中一个碱基匹配到gap的加分值[-1]
     */
    private static final int matchScore = 1;
    private static final int mismatchScore = 0;
    private static final int gap = -1;

    /**
     * 利用semi global alignment算法设定阈值进行匹配
     * @param subReadsList
     * @param markSeq
     * @return
     */
    public static List<String[]> alignmentAndCut(List<Object[]> subReadsList, Arguments arguments,
                                                 MappingInfo mappingInfo,String markSeq){
        // 获取fastq集合的大小;
        int listSize = subReadsList.size();
        // 获取markSeq(MmeI-ATTL-MmeI)的长度（66）;
        int markSeqLen = markSeq.length();
        // 获取 CutLength 的长度（默认为13bp）
        int cutLength =  arguments.getCutLength();
        // 创建输出的结果集合；
        List<String[]> resList = new LinkedList<>();
        // 创建循环遍历出来的reads参数以及其长度参数
        String reads;
        int readsLen;
        // 根据待匹配的reads长度来创建dp二维数组用于保存当前格子最佳打分值;
        int[][] dp;
        // 根据待匹配的reads长度来创建path二维数组用于保存当前格子最佳打分值的路径;
        int[][] path;
        // 将四种配对情况列出，delete -> Xi对比到gap上，insert -> Yi对比到gap上;
        int mismatch,delete,insert,match;
        // 其他参数
        int[] lastDpArr;
        int arrMaxNum,xSeqEndIndex;
        int x,y,insertCounts;
        int bestPathScores,xSeqBeginIndex;
        // 结果数组
        String[] res;
        // 遍历集合，将每一个元素的动态规划dp二维数组填写好，并且对每一个元素进行比对筛选;
        for (int i = 0; i < listSize; i++) {
            if ((boolean) subReadsList.get(i)[0] == true) continue;
            reads = String.valueOf(subReadsList.get(i)[1]);
            // 统计Reads长度
            readsLen = reads.length();
            // 加1是因为矩阵中还需要多一个位置给gap
            dp = new int[markSeqLen + 1][readsLen + 1];
            path = new int[markSeqLen + 1][readsLen + 1];

            // (1) 找到初试值：将二维数组第一行和第一列都设置为0,创建二维数组时就全部是是0元素了；

            // (2) 设置打分公式(关系式),并且对每个矩阵格子都进行计算,顺便将最佳路径也存储好
            // 对我们这个匹配问题来说，我们选择其中一种最佳路径来记录即可，因为需要的是最后的真实打分值用于筛选reads
            for (int j = 1; j < markSeqLen+1; j++) {
                for (int k = 1; k < readsLen+1; k++) {
                    delete = dp[j][k-1] + gap; // Xi对比到gap上,路径往右侧走;
                    insert = dp[j-1][k] + gap; // Yi对比到gap上,路径往下面走;
                    // 创建一个temp，临时比较最大值使用
                    int temp = Math.max(delete,insert);
                    // 判断格子对应的两个碱基是否能匹配上，根据结果进行不同的打分策略
                    if (markSeq.charAt(j-1) == reads.charAt(k-1)) {
                        match = dp[j-1][k-1] + matchScore;
                        // 往dp[][]二维数组中添加最佳值
                        dp[j][k] = Math.max(match,temp);

                        // 往path[][]二维数组汇总添加当前各自最佳值的路径
                        // 若是insert则标记为1，delete则标记为2，mismatch和match都为3；
                        if (dp[j][k] == match) {
                            // 如果路劲为3，则斜对角回溯
                            path[j][k] = 3;
                        } else {
                            // 如果路劲为2，则往左侧回溯
                            // 当然如果如果delete和insert值相同(两条最佳路径),我们选其中一条最佳路径即可;
                            if (dp[j][k] == delete) path[j][k] = 2;
                            // 如果路劲为1，则往上面回溯
                            else path[j][k] = 1;
                        }
                    } else {
                        mismatch = dp[j-1][k-1] + mismatchScore;
                        dp[j][k] = Math.max(mismatch,temp);
                        if (dp[j][k] == mismatch) {
                            path[j][k] = 3;
                        } else {
                            if (dp[j][k] == delete) path[j][k] = 2;
                            else path[j][k] = 1;
                        }
                    }
                }
            }


            // (3) 如果最佳打分值大于我们所设定的阈值即可取,
            // 并且截取x序列被匹配到的序列(MmeI-ATTL-MmeI)两侧 cutLength bp的碱基序列；

            // 1.先获取最后一行数组的最大值和最大值下标(x序列被匹配到的最后一个碱基的下标),
            // 然后判断后面是否还有 cutLength bp 的碱基，如果含有则截取 cutLength bp；
            lastDpArr = dp[markSeqLen];
            arrMaxNum = lastDpArr[0];
            xSeqEndIndex = 0;
            for (int j = 0; j < readsLen; j++) {
                if(lastDpArr[j] > arrMaxNum){ //比较后赋值
                    arrMaxNum = lastDpArr[j];
                    // 该xSeqEndIndex索引在x序列中就是被匹配序列的最后一个碱基的后一个碱基，
                    // 所以target子序列为subString(xSeqEndIndex,xSeqEndIndex+cutLength);
                    xSeqEndIndex = j;
                }
            }

            // 2.再根据最大值下标找到格子，再根据其中的路径来x序列被匹配到的第一个碱基的下标(根据路径达到第一行时的列坐标即是)
            x = xSeqEndIndex;
            y = markSeqLen;
            // 记录insertCount，需要加到最终最佳分数，
            // 因为insert -> Yi对比到gap上,相当于往下走了一格，并且还扣了一分;
            // 相当于最后结果少走了一格(少加一分)，并且扣了一分，相当于打分机制扣了两分;
            insertCounts = 0;
            // 需要回溯66次才能从最后一行回溯到第一行
            for (int j = 0; j <= markSeqLen; j++) {
                if (path[y][x] == 3) {
                    // 如果路劲为3，则斜对角回溯
                    x--;
                    y--;
                } else if (path[y][x] == 2) {
                    // 如果路劲为2，则往左侧回溯,
                    // 因为x序列多碱基，所以要往左走一次，
                    // 并且走的次数需要多一次，不然就无法走到第一行
                    x--;
                    j--;
                } else if (path[y][x] == 1){
                    // 如果路劲为1，则往上面回溯
                    // 因为先x序列少碱基，所以需要往上走一次，次数不变
                    y--;
                    insertCounts++;
                }
            }
            // 最后的最佳路径打分值等于arrMaxNum + insertCounts
            bestPathScores = arrMaxNum + insertCounts;
            // 判断最佳打分值是否达到我们的阈值，若不达标则进行下一个循环;
            if (bestPathScores < markSeqLen - arguments.getNotMatchNums()) continue;

            // 将匹配通过的序列的标记改成 true,以防反向标记的时候接着匹配；
            subReadsList.get(i)[0] = true;

            // 统计匹配上的reads的数量
            mappingInfo.getAlignToMarkSeqCounts().getAndIncrement();

            // 该xSeqBeginIndex索引在x序列中就是被匹配序列的第一个碱基，
            // 所以子序列为subString(xSeqBeginIndex-cutLength,xSeqBeginIndex);
            xSeqBeginIndex = x;

            // 判断markSeq前后两端是否还能截取 cutLength bp左右的reads出来,如果可以则截取两端 cutLength bp的子序列用作后面的比对使用；
            if (xSeqBeginIndex >= cutLength && xSeqEndIndex + cutLength < readsLen+1 ) {
                // 创建每个满足条件的输出的 cutLength bp字符串的数组,用于添加到resList输出集合中;
                res = new String[2];
                res[0] = reads.substring(xSeqBeginIndex-cutLength,xSeqBeginIndex);
                res[1] = reads.substring(xSeqEndIndex,xSeqEndIndex+cutLength);
                resList.add(res);
            }
        }
        return resList;
    }


    public static Map<String,Integer> getPPIMappingResult(List<String[]> subList_53, List<String[]> subList_35,
                                                          LibraryInfo libraryInfo, PPIResultInfo ppiResultInfo) {

        Map<String,Integer> PPI_Result = new HashMap<>(2000);
        int subList53Size = subList_53.size();
        int subList35Size  =subList_35.size();

        // 如果是subList_53(正向序列),左侧是bait（5’-3‘，正向）文库，右侧是prey(3'-5'，反向)文库；
        for (int i = 0; i < subList53Size; i++) {
            if (Utils.containsThisSeq_53(subList_53.get(i),libraryInfo)) {
                // 统计能够唯一比对的序列数
                ppiResultInfo.getMapPPIResultCounts().getAndIncrement();
                // 输出结果，prey基因名在左侧，bait基因名在右侧
                String PPI_Name = "{},{}";
                PPI_Name = StrUtil.format(PPI_Name,libraryInfo.getPreyLibraryReverseSeqAndName().get(subList_53.get(i)[1]),
                        libraryInfo.getBaitLibrarySeqAndName().get(subList_53.get(i)[0]));
                // 计数
                Utils.mapEleCounts(PPI_Result,PPI_Name);
            }
        }

        // 如果是subList_35(负向序列),左侧是prey（5’-3‘，正向）文库，右侧是bait(3'-5'，反向)文库；
        for (int i = 0; i < subList35Size; i++) {
            if (Utils.containsThisSeq_35(subList_35.get(i),libraryInfo)) {
                // 统计能够唯一比对的序列数
                ppiResultInfo.getMapPPIResultCounts().getAndIncrement();
                // 输出结果，prey基因名在左侧，bait基因名在右侧
                String PPI_Name = "{},{}";
                PPI_Name = StrUtil.format(PPI_Name,libraryInfo.getPreyLibrarySeqAndName().get(subList_35.get(i)[0]),
                        libraryInfo.getBaitLibraryReverseSeqAndName().get(subList_35.get(i)[1]));
                // 计数
                Utils.mapEleCounts(PPI_Result,PPI_Name);
            }
        }
        return PPI_Result;
    }
}


// alignmentAndCut:

//    // 标记序列时反向还是正向；
//    String str = MappingInfo.MMEI_ATTL_MMEI_5_3.equals(markSeq) ? "5'-3':" : "3'-5':";

//                System.out.println(StrUtil.format(Constant.ALIGNMENT_HAS_MATCHED,i));

//            // 遍历数组检查结果
//            for (int j = 0; j <= markSeqLen; j++){
//                System.out.println(Arrays.toString(dp[j]));
//            }
//            System.out.println();
//
//            for (int j = 0; j <= markSeqLen; j++){
//                System.out.println(Arrays.toString(path[j]));
//            }


//                System.out.println(StrUtil.format(Constant.ALIGNMENT_SCORES_NOT_ENOUGH,str,i,bestPathScores));


// 输出截出来的序列
//                System.out.println(StrUtil.format(Constant.ALIGNMENT_RIGHT,str,i,Arrays.toString(res),bestPathScores));


//            else System.out.println(StrUtil.format(Constant.ALIGNMENT_MARK_SEQ_TOO_SHORT,str,i,bestPathScores));





// getPPIMappingResult:

//                System.out.println(StrUtil.format(Constant.PPI_RIGHT_53,i,Arrays.toString(subList_53.get(i)),
//                        libraryInfo.getBaitLibrarySeqAndName().get(subList_53.get(i)[0]),
//                        libraryInfo.getPreyLibraryReverseSeqAndName().get(subList_53.get(i)[1])));


//            else System.out.println(StrUtil.format(Constant.PPI_NOT_MATCH_53,i,Arrays.toString(subList_53.get(i))));


//                System.out.println(StrUtil.format(Constant.PPI_RIGHT_35,i,Arrays.toString(subList_35.get(i)),
//                        libraryInfo.getPreyLibrarySeqAndName().get(subList_35.get(i)[0]),
//                        libraryInfo.getBaitLibraryReverseSeqAndName().get(subList_35.get(i)[1])));


//            else System.out.println(StrUtil.format(Constant.PPI_NOT_MATCH_35,i,Arrays.toString(subList_35.get(i))));