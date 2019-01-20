package de.hpi.isg.dataprep.metadata;

import de.hpi.isg.dataprep.exceptions.DuplicateMetadataException;
import de.hpi.isg.dataprep.exceptions.MetadataNotFoundException;
import de.hpi.isg.dataprep.exceptions.MetadataNotMatchException;
import de.hpi.isg.dataprep.exceptions.RuntimeMetadataException;
import de.hpi.isg.dataprep.model.repository.MetadataRepository;
import de.hpi.isg.dataprep.model.target.objects.Metadata;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Lukas Behrendt, Oliver Clasen, Lisa Ihde
 * @since 2019/01/19
 */
public class CSVSourcePath extends Metadata {
	
	private String csvSourcePath;
	
	private CSVSourcePath() {
		super(CSVSourcePath.class.getSimpleName());
	}
	
	public CSVSourcePath(String path) {
		this();
		csvSourcePath = path;
	}
	
	private String getCsvSourcePath() {
		return csvSourcePath;
	}
	
	@Override
	public void checkMetadata(MetadataRepository metadataRepository) throws RuntimeMetadataException {
		List<CSVSourcePath> matchedInRepo = metadataRepository.getMetadataPool().stream()
				.filter(metadata -> metadata instanceof CSVSourcePath)
				.map(metadata -> (CSVSourcePath) metadata)
				.collect(Collectors.toList());
		
		if (matchedInRepo.size() == 0) {
			throw new MetadataNotFoundException(String.format("Metadata %s not found in the repository.", getClass().getSimpleName()));
		} else if (matchedInRepo.size() > 1) {
			throw new DuplicateMetadataException(String.format("Multiple pieces of metadata %s found in the repository.", getClass().getSimpleName()));
		} else {
			CSVSourcePath metadataInRepo = matchedInRepo.get(0);
			if (!getCsvSourcePath().equals(metadataInRepo.getCsvSourcePath())) {
				throw new MetadataNotMatchException(String.format("Metadata value does not match that in the repository."));
			}
		}
	}
	
	@Override
	public boolean equalsByValue(Metadata metadata) {
		return false;
	}
	
	@Override
	public String getName() {
		return scope.getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (this.csvSourcePath == o.toString()) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(csvSourcePath);
	}
	
	@Override
	public String toString() {
		return csvSourcePath;
	}
}