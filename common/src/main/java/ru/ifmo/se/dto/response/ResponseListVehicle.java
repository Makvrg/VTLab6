package ru.ifmo.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.VehicleDto;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseListVehicle extends Response {

    public ResponseListVehicle(boolean status,
                               String message,
                               List<VehicleDto> vehicles) {
        super(status, message);
        this.vehicleDtoList = vehicles;
    }

    private List<VehicleDto> vehicleDtoList;
}
