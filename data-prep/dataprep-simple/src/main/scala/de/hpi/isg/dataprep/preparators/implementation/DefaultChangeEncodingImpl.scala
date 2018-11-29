package de.hpi.isg.dataprep.preparators.implementation

import de.hpi.isg.dataprep.components.PreparatorImpl
import de.hpi.isg.dataprep.model.error.{PreparationError, RecordError}
import de.hpi.isg.dataprep.model.target.system.AbstractPreparator
import de.hpi.isg.dataprep.preparators.define.ChangeEncoding
import de.hpi.isg.dataprep.{ConversionHelper, ExecutionContext}
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.util.CollectionAccumulator
import java.io.File
import java.nio.charset.{Charset, CodingErrorAction, StandardCharsets}
import java.nio.file.{Files, Path, StandardOpenOption}

import scala.io.Codec
import scala.util.Try
import scala.io.Source


/**
  *
  * @author Lan Jiang
  * @since 2018/8/29
  */
class DefaultChangeEncodingImpl extends PreparatorImpl {

    def resourceToString(file: File, inputEncoding: Charset = StandardCharsets.UTF_8) = Source.fromFile(file)(new Codec(inputEncoding)).mkString

    def writeFile(outputPath: Path, content: String, outputEncoding: Charset = StandardCharsets.UTF_8): Try[Path] = {

        val byteBuff = outputEncoding.encode(content)

        Try(Files.write(
            outputPath,
            byteBuff.array(),
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
        ))
    }

      /**
      * The abstract class of preparator implementation.
      *
      * @param abstractPreparator is the instance of { @link AbstractPreparator}. It needs to be converted to the corresponding subclass in the implementation body.
      * @param dataFrame        contains the intermediate dataset
      * @param errorAccumulator is the { @link CollectionAccumulator} to store preparation errors while executing the preparator.
      * @return an instance of { @link ExecutionContext} that includes the new dataset, and produced errors.
      * @throws Exception
      */
    override protected def executeLogic(abstractPreparator: AbstractPreparator, dataFrame: Dataset[Row], errorAccumulator: CollectionAccumulator[PreparationError]): ExecutionContext = {
        val preparator = abstractPreparator.asInstanceOf[ChangeEncoding]
        val propertyName = preparator.propertyName

        new ExecutionContext(dataFrame, errorAccumulator)
    }
}