package ru.ifmo.se.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.validator.ValidatorMessages;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {

    @NotNull(message = ValidatorMessages.X_COORD_MUST_BE_NOT_NULL)
    @Min(
            value = -481,
            message = ValidatorMessages.X_COORD_MUST_BE_MORE_MIN
    )
    private Integer x;

    private long y;

    @Override
    public String toString() {
        return String.format("Coordinates{x=%d, y=%d}", x, y);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Coordinates that)) {
            return false;
        }
        return Double.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
