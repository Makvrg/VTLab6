package ru.ifmo.se.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.VehicleTypeDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestType extends Request{

    public RequestType(String commandName,
                       VehicleTypeDto vehicleTypeDto) {
        super(commandName);
        this.vehicleTypeDto = vehicleTypeDto;
    }

    private VehicleTypeDto vehicleTypeDto;
}
