package ru.ifmo.se.dto.request;

import ru.ifmo.se.dto.entity.VehicleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestVehicle extends Request{

    public RequestVehicle(String commandName,
                          VehicleDto vehicleDto) {
        super(commandName);
        this.vehicleDto = vehicleDto;
    }

    private VehicleDto vehicleDto;
}
