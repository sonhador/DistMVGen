package com.dell.korea.psd.bigdata.mr;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class DistMVMapper extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        FileSystem fs = FileSystem.get(context.getConfiguration());
        Path srcFile = ((FileSplit) context.getInputSplit()).getPath();

        boolean isRename2 = context.getConfiguration().get("RENAME_TYPE").equals("rename2");

        if (isRename2) {
            DistributedFileSystem distFs = (DistributedFileSystem)fs;
            distFs.rename(srcFile, new Path(context.getConfiguration().get("DST_DIR") + "/" + srcFile.getName()), Options.Rename.OVERWRITE);
        } else {
            fs.rename(srcFile, new Path(context.getConfiguration().get("DST_DIR") + "/" + srcFile.getName()));
        }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.progress();
    }
}