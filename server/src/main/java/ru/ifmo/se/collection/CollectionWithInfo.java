package ru.ifmo.se.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.entity.Vehicle;

import java.util.Collection;
import java.util.Date;

@Getter
@RequiredArgsConstructor
public class CollectionWithInfo {

    private final Collection<Vehicle> collection;
    private final Date initializationDate = new Date();
    private final Class<?> collectionType;
    private final Class<?> elementsType;

    public int getCountOfElements() {
        return collection.size();
    }
}
