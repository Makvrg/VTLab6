package ru.ifmo.se.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.UserDto;
import ru.ifmo.se.dto.entity.VehicleDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestVehicle extends Request{

    public RequestVehicle(String commandName,
                          VehicleDto vehicleDto,
                          UserDto userDto) {
        super(commandName, userDto);
        this.vehicleDto = vehicleDto;
    }

    private VehicleDto vehicleDto;
}
