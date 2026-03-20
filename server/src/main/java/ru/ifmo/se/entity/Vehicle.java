package ru.ifmo.se.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.io.input.fileparser.CoordinatesConverter;
import ru.ifmo.se.validator.ValidatorMessages;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle implements Comparable<Vehicle> {

    @CsvBindByName(column = "id", required = true)
    @Min(
            value = 1L,
            message = ValidatorMessages.ID_MUST_BE_MORE_ZERO
    )
    private long id;

    @CsvBindByName(column = "name", required = true)
    @NotBlank(message = ValidatorMessages.NAME_MUST_BE_NON_BLANK)
    private String name;

    @CsvCustomBindByName(
            column = "coordinates",
            required = true,
            converter = CoordinatesConverter.class
    )
    @NotNull(message = ValidatorMessages.COORDS_MUST_BE_NOT_NULL)
    @Valid
    private Coordinates coordinates;

    @CsvBindByName(column = "creationDate", required = true)
    @CsvDate("dd-MM-yyyy HH:mm:ss")
    @NotNull(message = ValidatorMessages.CREATE_DATE_MUST_BE_NOT_NULL)
    private Date creationDate;

    @CsvBindByName(column = "enginePower", required = true)
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = ValidatorMessages.ENGINE_POWER_MUST_BE_MORE_ZERO
    )
    private double enginePower;

    @CsvBindByName(column = "distanceTravelled", required = true)
    @NotNull(message = ValidatorMessages.DISTANCE_TRAVELLED_MUST_BE_NOT_NULL)
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = ValidatorMessages.DISTANCE_TRAVELLED_MUST_BE_MORE_ZERO
    )
    private Float distanceTravelled;

    @CsvBindByName(column = "type", required = true)
    @NotNull(message = ValidatorMessages.VEHICLE_TYPE_MUST_BE_NOT_NULL)
    private VehicleType type;

    @CsvBindByName(column = "fuelType", required = true)
    @NotNull(message = ValidatorMessages.FUEL_TYPE_MUST_BE_NOT_NULL)
    private FuelType fuelType;

    @Getter
    public enum FieldNames {
        ID("id"),
        NAME("name"),
        X("x"),
        Y("y"),
        CREATION_DATE("creationDate"),
        ENGINE_POWER("enginePower"),
        DISTANCE_TRAVELLED("distanceTravelled"),
        TYPE("type"),
        FUEL_TYPE("fuelType");

        private final String title;

        FieldNames(String title) {
            this.title = title;
        }
    }

    @Override
    public int compareTo(Vehicle vehicle) {
        Double thisDigit = enginePower * distanceTravelled;
        Double vehicleDigit = vehicle.enginePower * vehicle.distanceTravelled;
        return thisDigit.compareTo(vehicleDigit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vehicle{")
                .append("id=").append(id)
                .append(", name='").append(name).append('\'')
                .append(", coordinates=").append(coordinates)
                .append(", creationDate=").append(creationDate)
                .append(", enginePower=").append(enginePower)
                .append(", distanceTravelled=").append(distanceTravelled)
                .append(", type=").append(type)
                .append(", fuelType=").append(fuelType)
                .append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Vehicle vehicle))
            return false;
        return Double.compare(enginePower, vehicle.enginePower) == 0 &&
                Objects.equals(name, vehicle.name) &&
                Objects.equals(coordinates, vehicle.coordinates) &&
                Objects.equals(creationDate, vehicle.creationDate) &&
                Objects.equals(distanceTravelled, vehicle.distanceTravelled) &&
                Objects.equals(type, vehicle.type) &&
                Objects.equals(fuelType, vehicle.fuelType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coordinates, creationDate,
                            distanceTravelled, type, fuelType);
    }
}
