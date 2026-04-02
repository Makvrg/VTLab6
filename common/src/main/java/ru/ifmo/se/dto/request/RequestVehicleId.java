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
public class RequestVehicleId extends Request {

    public RequestVehicleId(String commandName,
                            VehicleDto vehicleDto,
                            Long id,
                            UserDto userDto) {
        super(commandName, userDto);
        this.vehicleDto = vehicleDto;
        this.id = id;
    }

    private VehicleDto vehicleDto;
    private Long id;
}
