package org.nms.cs626;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Configured implements Tool {
    private static final Logger LOG = Logger.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new App(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf(), "hw1_part2");
        job.setJarByClass(this.getClass());
        // Use TextInputFormat, the default unless job.setInputFormatClass is used
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map  (LongWritable offset, Text lineText, Context context)
                throws IOException, InterruptedException {
            List<CSVRecord> records =  CSVParser
                    .parse(lineText.toString(),CSVFormat.DEFAULT)
                    .getRecords();
            CSVRecord inputLine = records.get(0);
            context.write(new Text(inputLine.get(0)),new IntWritable(-1));
            context.write(new Text(inputLine.get(1)),new IntWritable(1));
        }
    }

    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>{

        public void reduce(Text keyin, Iterable<IntWritable> valueIn,
                           Context context) throws IOException, InterruptedException {
            List<IntWritable> leftCount = new ArrayList<>();
            List<IntWritable> rightCount = new ArrayList<>();
            for(IntWritable value:valueIn){
                if(value.get() == -1) leftCount.add(value);
                if(value.get() == 1) rightCount.add(value);
            }
            if(leftCount.isEmpty() && !rightCount.isEmpty()){
                context.write(keyin,new IntWritable(rightCount.size()));
            }
        }
    }
}
