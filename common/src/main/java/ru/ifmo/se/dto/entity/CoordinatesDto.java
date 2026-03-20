package ru.ifmo.se.dto.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.fieldmessages.FieldMessages;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesDto implements Serializable {

    @NotNull(message = FieldMessages.X_COORD_MUST_BE_NOT_NULL)
    @Min(
            value = -481,
            message = FieldMessages.X_COORD_MUST_BE_MORE_MIN
    )
    private Integer x;

    private long y;
}
