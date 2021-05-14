# RLLNP

#### 介绍

**RLLNP软件主要利用半全局比对算法【semi-global alignment】和唯一比对算法【unique aligment】获取RLL-Y2H测序数据中的Prey文库和Bait文库之间的蛋白互作信息。** 

![输入图片说明](https://images.gitee.com/uploads/images/2021/0514/202237_30bbac23_7810647.png "屏幕截图.png")


#### 安装教程

1. 下载【RLLNP.jar】至本地，即可运行，无需安装；
2. 运行需要本地环境有Java开发工具包【JDK】或Java运行环境【JRE】（version >= 8）;
3. 使用JAVA虚拟机【JVM】中的【java -jar】命令进行运行即可；


#### 快速使用说明

```
// 1.查看用户文档
java -jar RLLNP.jar
java -jar RLLNP.jar -h
java -jar RLLNP.jar --help
```


```
// 2.查看软件版本
java -jar RLLNP.jar -v
java -jar RLLNP.jar --version
```

```
// 3.检查Prey文库和Bait文库信息
java -jar RLLNP.jar -a Prey.csv -b Bait.csv
```


```
// 4.利用半全局比对算法获取RLL-Y2H测序文件中的PPI信息
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f sequencing.fastq
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f sequencing.fastq.gz
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -t 8 -f sequencing.fq  
java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f sequencing.fq.gz -o PPI.csv
```


```
// 5.将多个PPI结果文件合并
java -jar RLLNP.jar -m merge.txt -o Total_PPI_result.csv
```

```
// 6.对比两个PPI结果文件之间的差异
java -jar RLLNP.jar -d PPI_result1.csv PPI_result2.csv
```



#### 实例教程

 **实例文件在【test】文件夹中：** 

1. 准备Prey文库信息和Bait文库信息，需要CSV文件（注意尽量不要有中文字符）。

>CSV文件中第一列应为基因名称，第二列应为基因序列，并且无需表头。
>
>例子可以参照【test】文件夹中的【Prey_test.csv】和【Bait_test.csv】文件。

2.利用RLLNP软件检查Prey文库文件和Bait文库文件中是否存在【基因名重复问题】或【基因末端序列重复问题】。
                                                                                                                                                                                                                                                                                               
```
// 质检结果会直接输出至控制台，可以用【>】符号进行IO重定向。
java -jar RLLNP.jar -a Prey_test.csv -b Bait_test.csv
```

3.1 利用半全局比对算法和唯一比对算法获取RLL-Y2H测序数据中的Prey文库和Bait文库之间的蛋白互作信息。

>文库质检步骤【2】可以与比对步骤一起进行，若Prey文库信息或Bait文库信息有误，则不会进行下面的比对步骤。

```
// 该命令下【-o】参数携带有默认属性：【./output/[-f参数所携带的文件名]_PPIResult.csv】
java -jar RLLNP.jar -a Prey_test.csv -b Bait_test.csv -f test.fq.gz 
```
3.2 同样可以加入【-o】参数来指定输出文件名

```
java -jar RLLNP.jar -a Prey_test.csv -b Bait_test.csv -f test.fq.gz -o ./output/test_PPIResult2.csv
```

4. 用以上的两组PPI结果文件来进行结果合并【test.fq.gz_PPIResult.csv】和【test_PPIResult2.csv】

>先准备【merge.txt】文件，用于存储两个PPI结果文件的【路径】

```
// merge.txt 文件内容如下：
[绝对路径/相对路径]/test.fq.gz_PPIResult.csv
[绝对路径/相对路径]/test_PPIResult2.csv
```
>再利用【-m】参数进行合并即可

```
// 若不使用【-o】参数，默写输出结果文件路径为【./output/Total_PPI_results.csv】
java -jar RLLNP.jar -m merge.txt -o Total.csv
```


#### 参数说明

![输入图片说明](https://images.gitee.com/uploads/images/2021/0514/163951_48580ddc_7810647.png "屏幕截图.png")

>（1）参数【-n】是指半全局比对算法中，reads与标记序列MmeI-ATTL-MmeI比对时允许错配的数量，默认允许错配8次；
>
>（2）参数【-l】是指唯一比对算法中，截取标记序列MmeI-ATTL-MmeI两端【13pb】数量的子序列对，将两端的子序列分别和质检好的文库序列末端的【13bp】进行唯一比对，只留下能够分别与Prey文库和Bait文库唯一比对的子序列对，默认接触数量为【13bp】

#### 注意事项

1.输出的PPI结果文件是CSV格式的，并且编码格式为【utf-8】。若用EXCEL打开中文字符有可能会出现乱码现象，可以先自行转码或尽量不要再基因名中出现中文字符。





