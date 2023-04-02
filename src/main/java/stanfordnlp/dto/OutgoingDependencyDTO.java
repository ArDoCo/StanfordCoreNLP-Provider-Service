/* Licensed under MIT 2023. */
package stanfordnlp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class OutgoingDependencyDTO {
    private DependencyType dependencyType;
    private long targetWordId;

    @JsonProperty("dependencyType")
    public DependencyType getDependencyType() {
        return dependencyType;
    }

    @JsonProperty("dependencyType")
    public void setDependencyType(DependencyType value) {
        this.dependencyType = value;
    }

    @JsonProperty("targetWordId")
    public long getTargetWordId() {
        return targetWordId;
    }

    @JsonProperty("targetWordId")
    public void setTargetWordId(long value) {
        this.targetWordId = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OutgoingDependencyDTO that = (OutgoingDependencyDTO) o;
        return targetWordId == that.targetWordId && Objects.equals(dependencyType, that.dependencyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencyType, targetWordId);
    }
}
