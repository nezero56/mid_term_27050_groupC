package rw.churchmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.churchmanagement.model.Location.LocationType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private String code;
    private String name;
    private LocationType locationType;
    private UUID parentId;
}
