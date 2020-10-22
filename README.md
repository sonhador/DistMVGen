# DistMVGen
Distributed mv operation with generated files.

args: <src_dir> <dst_dir> <src_dir_num_files_to_generate> <rename/rename2>

rename2: 
* files generated in src dir are also generated the same in dst dir.
* uses 'overwrite' option for rename2 operation.

ex> hadoop jar target/distmv_gen-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp/src /tmp/dst 1000 rename2
