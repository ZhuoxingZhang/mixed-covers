# Introduction
This repository contains various artifacts, such as source code, experimental results, and other materials, that supplement our work on mixed covers.\
&nbsp;&nbsp;&nbsp;&nbsp;For all experiment code on our paper, you can visit <kbd>exp/</kbd> to look for, like experiment 1, experiment 2, ...\
In the following sections, we describe how our experiments can be reproduced. 
# Preliminaries: Getting databases ready for experiments
> 1. Import 14 datasets as SQL databases
>> We have used MySQL 8.0 as database workbench. Firstly, please create a database called "freeman". Afterwards, import the [14 datasets](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html) as MySQL databases by setting column names as 0,1,...,n-1 where n is the number of columns in a given dataset. In addition, please create a column named "id" as an auto_increment attribute for each table.
>2. Functional dependencies (FDs)
>> For each of the 14 datasets, we compute all FD covers and mixed covers. You can find all FD covers/mixed covers given as separate json files in <kbd>Artifact/FD/</kbd>.
>3. JDK & JDBC
>> Our code was developed in JAVA. As a consequence, please specify a JDK with version 8 or later. At the moment, we are using JDBC (version 8.0.26) as a connector to MySQL databases.
# Experiments
Our experiments are organized into seven sections. For each of them, you can run different code/scripts:
>0. Computing FD covers
>> In this experiment, we compute and save several FD coverx with given FD as input. These FD covers include non-redundant, reduced, canonical, minimal and reduced minimal covers. Then, we statistics some properties when computing these FD covers. Your can run source code in <kbd>exp/exp0</kbd> on mainstream IDEs like eclipse or IDEA.
>1. Impact of FD covers on computing minimal keys
>> In this experiment, we investigate the influence (time required) of different FD covers as input on computing minimal keys. The algorithm to compute minimal keys is a P-time algorithm, proposed by Osborne. Your can run source code in <kbd>exp/exp1</kbd>.
> 2. Computing mixed covers
>> Given an FD set, such as original, non-redundant,... This experiment computes and saves Key/FD cover, with given FD set as input, such as original, non-redundant, reduced, ... Then, the experiment statistics some properties when computing mixed covers. 
> 4. Performance of Algorithms
>> 4.1 Real-world
>>> Before running these experiments, set some parameters in <kbd>conf/Constant.java</kbd> to access some datasets, and to turn enable/disable some options. Afterwards, you can run <kbd>conf/Main.java</kbd> to start the experiment. Our results are shown in <kbd>Artifact/02 - Experiments/4 - Performance of Algorithms/Real-world/</kbd>.

>> 4.2 Lineitem
>>> The performance comparison of Alg. 1 over Alg. 2 is done in <kbd>additional/SubschemaPerfExp.java</kbd>. Before starting this experiment, create a new file based on the sub-schemata of the decomposition result from Experiment 4.1 for the lineitem dataset that are in BCNF. Then configure the main function and start. Our results are shown in <kbd>Artifact/02 - Experiments/4 - Performance of Algorithms/Lineitem/</kbd>.

>> 4.3 RunningExample
>>> For experiments with our running example, as introduced in our paper, we have placed our experimental results, schema definitions, stored procedures, and SQL scripts for update and query operations in <kbd>Artifact/02 - Experiments/4 - Performance of Algorithms/RunningExample/</kbd>. These can be repeated by creating a database for each of the two decompositions (3NF vs 2-CONF), populating them with different numbers of records, and then running the operations. 
> 5. PTIME BCNF
>> In this experiment, we analyzed the cost of conducting BCNF decompositions that are guaranteed to be in polynomial deterministic time in the input. We implemented Tsou and Fischer's classical PTIME lossless BCNF-decomposition algorithm in <kbd>additional/LosslessJoinDecompIntoBCNF.java</kbd>. Before the execution, set up the corresponding dataset information in <kbd>conf/Constant.java</kbd>, and some other variables in the main function of <kbd>additional/LosslessJoinDecompIntoBCNF.java</kbd>. After this you can start the experiment. Finally, you can check the folder <kbd>Artifact/02 - Experiments/5 - PTIME BCNF/</kbd> for our experimental statistics and logs.
