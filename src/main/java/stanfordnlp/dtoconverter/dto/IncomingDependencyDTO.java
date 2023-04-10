/* Licensed under MIT 2023. */
package stanfordnlp.dtoconverter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.kastel.mcse.ardoco.core.api.data.text.DependencyTag;

import java.util.Objects;

public class IncomingDependencyDTO {
    private DependencyTag dependencyType;
    private long sourceWordId;

    @JsonProperty("dependencyType")
    public DependencyTag getDependencyTag() {
        return dependencyType;
    }

    @JsonProperty("dependencyType")
    public void setDependencyTag(DependencyTag value) {
        this.dependencyType = value;
    }

    @JsonProperty("sourceWordId")
    public long getSourceWordId() {
        return sourceWordId;
    }

    @JsonProperty("sourceWordId")
    public void setSourceWordId(long value) {
        this.sourceWordId = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IncomingDependencyDTO that = (IncomingDependencyDTO) o;
        return sourceWordId == that.sourceWordId && dependencyType == that.dependencyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencyType, sourceWordId);
    }
}
