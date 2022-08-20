**Introduce**

The RLLNP tool mainly uses the semi-global alignment algorithm and the unique alignment algorithm to obtain the protein interaction information between the prey library and the bait library in the RLL-Y2H sequencing data.

![image](https://user-images.githubusercontent.com/45482470/185730889-fc675dc0-a80e-433d-ab1d-339bd66fa344.png)


**Installation tutorial**

1.Download [RLLNP.jar] to the local without installation.

2.The Java Development Kit and Java Runtime Environment are required for local environment.
(The RLLNP file is written using JDK8, please make the local running environment version greater than or equal to JDK8)

3.Use the [Java - jar] command in the Java Virtual Machine to run.

**Quick instructions**

     // 1. View user documentation
     java -jar RLLNP.jar
     java -jar RLLNP.jar -h

    // 2.  View software version
    java -jar RLLNP.jar -v

    // 3. Check the pre library and bait library information
    java -jar RLLNP.jar -a Prey.csv -b Bait.csv

    // 4. Obtaining PPIs information in RLL-Y2H sequencing files using a semi-global Alignment Algorithm
    java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f test.fastq
    java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f test.fastq.gz
    java -jar RLLNP.jar -a Prey.csv -b Bait.csv -t 8 -f test.fq
    java -jar RLLNP.jar -a Prey.csv -b Bait.csv -f test.fq.gz -o PPIs.csv

    // 5. Merge multiple PPIs result files
    java -jar RLLNP.jar -m merge.txt
    
    // 6. Compare the differences between the two PPIs result files
    java -jar RLLNP.jar -d PPIs_result1.csv PPIs_result2.csv

**Example tutorial**

(The file is located in the test folder)

1.CSV files are required to prepare the prey library and the bait library information (please try not to have Chinese characters).
The first column in the CSV file should be gene name, the second column should be gene sequence, and no header required.
For example, please refer to the [pre_test. CSV] and [bait_test. CSV] files in the test folder.

2.Use RLLNP tool to check whether there is gene name duplication problem or gene end sequence duplication problem in the prey library file and the bait library file.

    //The quality inspection results will be directly output to the console. You can use the [>] symbol to redirect IO.
    java -jar RLLNP. jar -a Prey_ test. csv -b Bait_ test.csv

3.1. Use the semi global alignment algorithm and the unique alignment algorithm were used to obtain the protein interaction information between the prey library and the bait library in the RLL-Y2H sequencing data.

The library quality inspection step [2] can be carried out together with the comparison step. If the pre library information or the bait library information is wrong, the following comparison step will not be carried out.

    //The [-o] parameter of this command carries the default attribute: [./output/[-f parameter carries the file name]_ PPIsResult.csv]
    java -jar RLLNP. jar -a Prey_ test. csv -b Bait_ test. csv -f test.fq. gz

3.2. The [-o] parameter can also be added to specify the output file name

    java -jar RLLNP. jar -a Prey_ test. csv -b Bait_ test. csv -f test.fq. gz -o ./output/test_ PPIsResult2.csv

4.Use the above two groups of PPIs result files to merge the results [test.fq.gz_PPIsResult.csv] and [test_PPIsResult2.csv]

First prepare the [merge. txt] file to store the [path] of the two PPIs result files

    // merge. txt file is as follows:
    [absolute path / relative path] / test.fq.gz_ PPIsResult.csv
    [absolute path / relative path] / test_ PPIsResult2.csv

Then use the [-m] parameter to merge

    //If the [-o] parameter is not used, the default output result file path is [./output/total_ppi_results.csv]
    java -jar RLLNP.jar -m merge.txt -o Total.csv

**Parameter description**

![image](https://user-images.githubusercontent.com/45482470/185730916-be17c777-7d9c-4ac6-824a-85f8c8e92c61.png)

Parameter [-n] refers to the number of mismatches allowed when the reads are aligned with the marker sequence _MmeI-ALLT-MmeI_ in the semi global alignment algorithm. By default, 8 mismatches are allowed.

Parameter [-l] refers to that in the unique alignment algorithm, the number of subsequence pairs at both ends of the marker sequence _MmeI-ATTL-MmeI_ is intercepted, and the subsequences at both ends are uniquely aligned with the [13bp] at the end of the library sequence after quality inspection. Only the subsequence pairs that can be uniquely aligned with the prey library and the bait library are left. The default contact number is [13bp].

**Note**

1.The library information files of the prey library and the bait library don’t need a header. If there is a header, the RLLNP software will report an error when running.

2.The output PPIs result file is in CSV format and the encoding format is [UTF-8]. If you use Excel to open Chinese characters, there may be confusion. You can transcode yourself first or try not to appear Chinese characters in the gene name.
