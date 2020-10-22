package com.dell.korea.psd.bigdata.mr;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.InvalidJobConfException;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class OverWritableOutputFormat extends TextOutputFormat {
    @Override
    public void checkOutputSpecs(JobContext job) throws IOException {
        Path outDir = getOutputPath(job);
        if (outDir == null) {
            throw new InvalidJobConfException("Output directory not set.");
        }
    }
}
