package de.hpi.isg.dataprep.preparators;

import de.hpi.isg.dataprep.components.Pipeline;
import de.hpi.isg.dataprep.components.Preparation;
import de.hpi.isg.dataprep.model.target.preparator.Preparator;
import de.hpi.isg.dataprep.model.target.system.AbstractPipeline;
import de.hpi.isg.dataprep.model.target.system.AbstractPreparation;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Lan Jiang
 * @since 2018/6/4
 */
public class PipelineTest extends PreparatorTest {

    @Test
    public void testPipelineOnRestaurants() throws Exception {
        pipeline.getRawData().show();

        Preparator prep1 = new ReplaceSubstring("phone", "/", "-");
        AbstractPreparation preparation1 = new Preparation(prep1);
        pipeline.addPreparation(preparation1);

        Preparator prep2 = new ReplaceSubstring("type", "[(\\s[0-9]+/[0-9]+-[0-9]+\\s)]", "");
        AbstractPreparation preparation2 = new Preparation(prep2);
        pipeline.addPreparation(preparation2);

        Preparator prep3 = new MoveProperty("id", 0);
        AbstractPreparation preparation3 = new Preparation(prep3);
        pipeline.addPreparation(preparation3);

        Preparator prep4 = new MoveProperty("type", 1);
        AbstractPreparation preparation4 = new Preparation(prep4);
        pipeline.addPreparation(preparation4);

        Preparator prep5 = new DeleteProperty("merged_values");
        AbstractPreparation preparation5 = new Preparation(prep5);
        pipeline.addPreparation(preparation5);

        pipeline.executePipeline();

        pipeline.getRawData().show();
    }

    @Test
    public void testPipelineOnPokemon() throws Exception {
        pipeline.getRawData().show();
        pipeline.getRawData().printSchema();

        Preparator prep1 = new ReplaceSubstring("identifier", "[(\\s)+]", "");
        AbstractPreparation preparation1 = new Preparation(prep1);
        pipeline.addPreparation(preparation1);

        Preparator prep2 = new ReplaceSubstring("id", "three", "3");
        AbstractPreparation preparation2 = new Preparation(prep2);
        pipeline.addPreparation(preparation2);

        pipeline.executePipeline();

        pipeline.getRawData().show();
    }

    //    @Test
//    public void testShortPipeline() throws Exception {
//        Preparator preparator1 = new ChangeDataType("id", DataType.PropertyType.STRING, DataType.PropertyType.INTEGER);
//        PreparationOld preparation1 = new PreparationOld(preparator1);
//        pipeline.addPreparation(preparation1);
//
//        Preparator preparator2 = new ChangeDataType("id", DataType.PropertyType.INTEGER, DataType.PropertyType.STRING);
//        PreparationOld preparation2 = new PreparationOld(preparator2);
//        pipeline.addPreparation(preparation2);
//
//        pipeline.executePipeline();
//
//        List<ErrorLog> trueErrorlogs = new ArrayList<>();
//
//        ErrorLog errorLog1 = new PreparationErrorLog(preparation1, "three", new NumberFormatException("For input string: \"three\""));
//        ErrorLog errorLog2 = new PreparationErrorLog(preparation1, "six", new NumberFormatException("For input string: \"six\""));
//        ErrorLog errorLog3 = new PreparationErrorLog(preparation1, "ten", new NumberFormatException("For input string: \"ten\""));
//
//        trueErrorlogs.add(errorLog1);
//        trueErrorlogs.add(errorLog2);
//        trueErrorlogs.add(errorLog3);
//
//        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);
//
//        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
//
//        Dataset<Row> updated = pipeline.getRawData();
//        StructType updatedSchema = updated.schema();
//
//        StructType trueSchema = new StructType(new StructField[] {
//                new StructField("id", DataTypes.StringType, true, Metadata.empty()),
//                new StructField("identifier", DataTypes.StringType, true, Metadata.empty()),
//                new StructField("species_id", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("height", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("weight", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("base_experience", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("order", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("is_default", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("date", DataTypes.StringType, true, Metadata.empty()),
//        });
//
//        // Second test whether the schema is correctly updated.
//        Assert.assertEquals(trueSchema, updatedSchema);
//        Assert.assertEquals(updated.count(), 7L);
//        Assert.assertEquals(updatedSchema.size(), 9);
//    }
//
//    @Test
//    public void testShortPipelineWithMetadataNotMatch() throws Exception {
//        Preparator preparator1 = new ChangeDataType("id", DataType.PropertyType.STRING, DataType.PropertyType.INTEGER);
//        PreparationOld preparation1 = new PreparationOld(preparator1);
//        pipeline.addPreparation(preparation1);
//
//        Preparator preparator2 = new ChangeDataType("id", DataType.PropertyType.INTEGER, DataType.PropertyType.STRING);
//        PreparationOld preparation2 = new PreparationOld(preparator2);
//        pipeline.addPreparation(preparation2);
//
//        Preparator preparator3 = new ChangeDataType("id", DataType.PropertyType.INTEGER, DataType.PropertyType.DOUBLE);
//        PreparationOld preparation3 = new PreparationOld(preparator3);
//        pipeline.addPreparation(preparation3);
//
//        pipeline.executePipeline();
//
//        List<ErrorLog> trueErrorlogs = new ArrayList<>();
//
//        ErrorLog pipelineError1 = new PipelineErrorLog(pipeline, preparation3,
//                new MetadataNotMatchException(String.format("Metadata value does not match that in the repository.")));
//
//        trueErrorlogs.add(pipelineError1);
//
//        ErrorLog errorLog1 = new PreparationErrorLog(preparation1, "three", new NumberFormatException("For input string: \"three\""));
//        ErrorLog errorLog2 = new PreparationErrorLog(preparation1, "six", new NumberFormatException("For input string: \"six\""));
//        ErrorLog errorLog3 = new PreparationErrorLog(preparation1, "ten", new NumberFormatException("For input string: \"ten\""));
//
//        trueErrorlogs.add(errorLog1);
//        trueErrorlogs.add(errorLog2);
//        trueErrorlogs.add(errorLog3);
//
//        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);
//
//        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
//
//        Dataset<Row> updated = pipeline.getRawData();
//        StructType updatedSchema = updated.schema();
//
//        StructType trueSchema = new StructType(new StructField[] {
//                new StructField("id", DataTypes.StringType, true, Metadata.empty()),
//                new StructField("identifier", DataTypes.StringType, true, Metadata.empty()),
//                new StructField("species_id", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("height", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("weight", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("base_experience", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("order", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("is_default", DataTypes.IntegerType, true, Metadata.empty()),
//                new StructField("date", DataTypes.StringType, true, Metadata.empty()),
//        });
//
//        // Second test whether the schema is correctly updated.
//        Assert.assertEquals(trueSchema, updatedSchema);
//        Assert.assertEquals(updated.count(), 7L);
//        Assert.assertEquals(updatedSchema.size(), 9);
//    }
}