package de.hpi.isg.dataprep.implementation;

import de.hpi.isg.dataprep.Consequences;
import de.hpi.isg.dataprep.model.error.PreparationError;
import de.hpi.isg.dataprep.model.target.preparator.Preparator;
import de.hpi.isg.dataprep.model.target.preparator.PreparatorImpl;
import de.hpi.isg.dataprep.preparators.MoveProperty;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.util.CollectionAccumulator;

/**
 * The super class for all the concrete {@link MoveProperty} preparator implementations.
 *
 * @author Lan Jiang
 * @since 2018/8/21
 */
abstract public class MovePropertyImpl extends PreparatorImpl {

    abstract protected Consequences executeLogic(MoveProperty preparator,
                                                 Dataset<Row> dataFrame,
                                                 CollectionAccumulator<PreparationError> errorAccumulator);

    @Override
    protected Consequences executePreparator(Preparator preparator, Dataset<Row> dataFrame) throws Exception {
        MoveProperty preparator_ = this.getPreparatorInstance(preparator, MoveProperty.class);
        CollectionAccumulator<PreparationError> errorAccumulator =
                this.createErrorAccumulator(preparator_, dataFrame);
        return this.executeLogic(preparator_, dataFrame, errorAccumulator);
    }

    @Override
    protected CollectionAccumulator<PreparationError> createErrorAccumulator(Preparator preparator, Dataset<Row> dataFrame) {
        CollectionAccumulator<PreparationError> errorAccumulator = new CollectionAccumulator<>();
        dataFrame.sparkSession().sparkContext().register(errorAccumulator,
                String.format("%s error accumulator.", ((MoveProperty)preparator).getClass().getSimpleName()));
        return errorAccumulator;
    }
}