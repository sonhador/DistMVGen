package com.dell.korea.psd.bigdata.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DistMVGen {
    private LinkedBlockingQueue<Integer> fileGenQueue = new LinkedBlockingQueue<>();
    private List<Thread> threads = new ArrayList<>();
    private int THREADS = 200;

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("args: <src_dir> <dst_dir> <src_dir_num_files_to_generate>");
            System.exit(1);
        }

        new DistMVGen(args);
    }

    public DistMVGen(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();

        conf.set("yarn.app.mapreduce.am.commands-opts", "-Xmx2048m");
        conf.setInt("mapreduce.task.io.sort.mb", 128);
        conf.setInt("mapreduce.map.memory.mb", 1024);
        conf.set("mapred.child.java.opts", "-Xmx1000m");

        conf.set("SRC_DIR", args[0]);
        conf.set("DST_DIR", args[1]);

        Job job = Job.getInstance(conf, "DistMVGen");
        job.setJarByClass(DistMVGen.class);
        job.setMapperClass(DistMVMapper.class);
        job.setNumReduceTasks(0);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        generateFiles(args[0], Integer.parseInt(args[2]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    private void generateFiles(String srcDir, int numFiles) throws InterruptedException {
        for (int i = 0; i < THREADS; i++) {
            fileGenQueue.add(i);
            threads.add(new Thread(new FileGen(srcDir)));
        }

        for (int i = 0; i < THREADS; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < THREADS; i++) {
            threads.get(i).join();
        }
    }

    private class FileGen implements Runnable {
        private String dir;

        public FileGen(String dir) {
            this.dir = dir;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Integer work = fileGenQueue.poll(1, TimeUnit.SECONDS);
                    if (work == null) {
                        break;
                    }

                    FileSystem fs = FileSystem.get(new Configuration());
                    FSDataOutputStream out = fs.create(new Path(dir + "/" + UUID.randomUUID().toString() + ".bin"));
                    out.write(new byte[1024]);
                    out.close();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}