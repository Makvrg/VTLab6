package ru.ifmo.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.VehicleDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVehicle extends Response {

    public ResponseVehicle(boolean status,
                           String message,
                           VehicleDto vehicleDto) {
        super(status, message);
        this.vehicleDto = vehicleDto;
    }

    private VehicleDto vehicleDto;
}
